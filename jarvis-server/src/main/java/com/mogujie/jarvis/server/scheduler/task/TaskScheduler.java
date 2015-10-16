/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:50:42
 */

package com.mogujie.jarvis.server.scheduler.task;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.core.util.ThreadUtils;
import com.mogujie.jarvis.dao.AppMapper;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dao.TaskMapper;
import com.mogujie.jarvis.dto.App;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.Task;
import com.mogujie.jarvis.server.TaskManager;
import com.mogujie.jarvis.server.TaskQueue;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.KilledEvent;
import com.mogujie.jarvis.server.scheduler.event.RunningEvent;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.service.TaskService;

/**
 * Scheduler used to handle ready tasks.
 *
 * @author guangming
 *
 */
@Repository
public class TaskScheduler extends Scheduler {
    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private AppMapper appMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private TaskQueue taskQueue;

    private Map<Long, DAGTask> readyTable = new ConcurrentHashMap<Long, DAGTask>();

    private PriorityBlockingQueue<FailedTask> failedQueue = new PriorityBlockingQueue<>(10,
            new Comparator<FailedTask>() {
        @Override
        public int compare(FailedTask t1, FailedTask t2) {
            return (int)(t1.getNextStartTime() - t2.getNextStartTime());
        }
    });

    class FailedScanThread extends Thread {
        public FailedScanThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                while (!failedQueue.isEmpty()) {
                    FailedTask failedTask = failedQueue.peek();
                    long currentTime = System.currentTimeMillis();
                    if (failedTask.getNextStartTime() <= currentTime) {
                        retryTask(failedTask.getTask());
                        failedQueue.poll();
                    } else {
                        break;
                    }
                }
                ThreadUtils.sleep(1000);
            }
        }
    }
    private FailedScanThread scanThread;

    private boolean isTestMode = SchedulerUtil.isTestMode();

    // unique taskid
    private AtomicLong maxid = new AtomicLong(1);
    private static final int DAFAULT_MAX_FAILED_ATTEMPTS = 3;
    private static final int DAFAULT_FAILED_INTERVAL = 1000;

    @Override
    @Transactional
    protected void init() {
        getSchedulerController().register(this);

        // load all READY tasks from DB
        List<Task> tasks = taskService.getTasksByStatus(JobStatus.READY);
        if (tasks != null) {
            for (Task task : tasks) {
                DAGTask dagTask = new DAGTask(task.getJobId(), task.getTaskId(), task.getAttemptId());
                readyTable.put(task.getTaskId(), dagTask);
            }
        }

        scanThread = new FailedScanThread("FailedScanThread");
        scanThread.start();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void destroy() {
        clear();
        getSchedulerController().unregister(this);
        scanThread.stop();
    }

    @Override
    public void handleStartEvent(StartEvent event) {
    }

    @Override
    public void handleStopEvent(StopEvent event) {
    }

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void handleSuccessEvent(SuccessEvent e) {
        long taskId = e.getTaskId();
        updateJobStatus(taskId, JobStatus.SUCCESS);
        readyTable.remove(taskId);
        reduceTaskNum(e.getJobId());
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleRunningEvent(RunningEvent e) {
        long taskId = e.getTaskId();
        updateJobStatus(taskId, JobStatus.RUNNING);
    }

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void handleKilledEvent(KilledEvent e) {
        long taskId = e.getTaskId();
        updateJobStatus(taskId, JobStatus.KILLED);
        readyTable.remove(taskId);
        reduceTaskNum(e.getJobId());
    }

    @Subscribe
    @AllowConcurrentEvents
    @Transactional
    public void handleFailedEvent(FailedEvent e) {
        DAGTask dagTask = readyTable.get(e.getTaskId());
        long taskId = e.getTaskId();
        if (dagTask != null) {
            int maxFailedAttempts = DAFAULT_MAX_FAILED_ATTEMPTS;
            int failedInterval = DAFAULT_FAILED_INTERVAL;
            if (!isTestMode) {
                Job job = jobMapper.selectByPrimaryKey(dagTask.getJobId());
                maxFailedAttempts = job.getFailedAttempts();
                failedInterval = job.getFailedInterval();
            }
            if (dagTask.getAttemptId() <= maxFailedAttempts) {
                int attemptId = dagTask.getAttemptId();
                attemptId++;
                dagTask.setAttemptId(attemptId);
                long currentTime = System.currentTimeMillis();
                long nextStartTime = (currentTime / 1000 + failedInterval) * 1000;
                if (!isTestMode) {
                    Task task = taskMapper.selectByPrimaryKey(taskId);
                    if (task != null) {
                        failedQueue.put(new FailedTask(task, nextStartTime));
                    }
                }
            } else {
                updateJobStatus(e.getTaskId(), JobStatus.FAILED);
                readyTable.remove(taskId);
            }
        }

        reduceTaskNum(e.getJobId());
    }

    private void updateJobStatus(long taskId, JobStatus status) {
        if (!isTestMode) {
            if (status.equals(JobStatus.RUNNING)) {
                taskService.updateStatusWithStart(taskId, status);
            } else if (status.getValue() == JobStatus.SUCCESS.getValue() || status.getValue() == JobStatus.FAILED.getValue()
                    || status.getValue() == JobStatus.KILLED.getValue()) {
                taskService.updateStatusWithEnd(taskId, status);
            }
        }
    }

    @VisibleForTesting
    public void clear() {
        readyTable.clear();
        maxid.set(1);
    }

    @VisibleForTesting
    public Map<Long, DAGTask> getReadyTable() {
        return readyTable;
    }

    public long submitJob(long jobId) {
        long taskId;
        if (!isTestMode) {
            Task task = createNewTask(jobId);
            taskMapper.insert(task);
            taskId = task.getTaskId();
        } else {
            taskId = generateTaskId();
        }

        submitTask(new DAGTask(jobId, taskId));
        return taskId;
    }

    public void retryTask(long taskId) {
        Task task = taskMapper.selectByPrimaryKey(taskId);
        DAGTask dagTask = readyTable.get(taskId);
        if (task != null && dagTask != null) {
            int attemptId = dagTask.getAttemptId();
            attemptId++;
            dagTask.setAttemptId(attemptId);
            retryTask(task);
        }
    }

    private void retryTask(Task task) {
        long taskId = task.getTaskId();
        DAGTask dagTask = readyTable.get(taskId);
        if (dagTask != null) {
            if (!isTestMode) {
                task.setAttemptId(dagTask.getAttemptId());
                DateTime dt = DateTime.now();
                Date currentTime = dt.toDate();
                task.setUpdateTime(currentTime);
                taskMapper.updateByPrimaryKey(task);
            }

            submitTask(dagTask);
        }
    }

    private void submitTask(DAGTask dagTask) {
        // add to readyTable
        if (!readyTable.containsKey(dagTask.getTaskId())) {
            readyTable.put(dagTask.getTaskId(), dagTask);
        }

        // submit to TaskQueue
        TaskDetail taskDetail = getTaskInfo(dagTask);
        if (taskDetail != null) {
            taskQueue.put(taskDetail);
        }
    }

    private Task createNewTask(long jobId) {
        Job job = jobMapper.selectByPrimaryKey(jobId);
        Task task = new Task();
        task.setJobId(jobId);
        task.setAttemptId(1);
        task.setExecuteUser(job.getSubmitUser());
        task.setJobContent(job.getContent());
        task.setStatus(JobStatus.READY.getValue());
        DateTime dt = DateTime.now();
        Date currentTime = dt.toDate();
        task.setCreateTime(currentTime);
        task.setUpdateTime(currentTime);

        return task;
    }

    // 重启的时候maxid会重置, for testing
    private long generateTaskId() {
        return maxid.getAndIncrement();
    }

    private TaskDetail getTaskInfo(DAGTask dagTask) {
        String fullId = dagTask.getJobId() + "_" + dagTask.getTaskId() + "_" + dagTask.getAttemptId();
        TaskDetail taskDetail = null;
        if (!isTestMode) {
            Job job = jobMapper.selectByPrimaryKey(dagTask.getJobId());
            App app = appMapper.selectByPrimaryKey(job.getAppId());
            taskDetail = TaskDetail.newTaskDetailBuilder()
                    .setFullId(fullId)
                    .setTaskName(job.getJobName())
                    .setAppName(app.getAppName())
                    .setUser(job.getSubmitUser())
                    .setPriority(job.getPriority())
                    .setContent(job.getContent())
                    .setTaskType(job.getJobType())
                    .setParameters(JsonHelper.parseJSON2Map(job.getParams()))
                    .build();
        }

        return taskDetail;
    }

    private void reduceTaskNum(long jobId) {
        if (!isTestMode) {
            Job job = jobMapper.selectByPrimaryKey(jobId);
            taskManager.appCounterDecrement(job.getAppId());
        }
    }
}
