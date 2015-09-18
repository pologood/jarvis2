/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:50:42
 */

package com.mogujie.jarvis.server.scheduler.task;

import java.lang.Thread.State;
import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.common.util.JsonHelper;
import com.mogujie.jarvis.core.common.util.ThreadUtils;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dao.TaskMapper;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.Task;
import com.mogujie.jarvis.server.TaskQueue;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.InitEvent;
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
@Service
public class TaskScheduler implements Scheduler {
    @Autowired
    JobMapper jobMapper;

    @Autowired
    TaskMapper taskMapper;

    @Autowired
    TaskService taskService;

    // for testing
    private static TaskScheduler instance = new TaskScheduler();
    private TaskScheduler() {
    }
    public static TaskScheduler getInstance() {
        return instance;
    }

    private ScanThread scanThread;

    class ScanThread extends Thread {
        private TaskQueue taskQueue = TaskQueue.getInstance();

        @Override
        public void run() {
            while (true) {
                while (!runnableQueue.isEmpty() && runningTasks.get() < maxConcurrentNum) {
                    DAGTask priorityTask = runnableQueue.poll();
                    com.mogujie.jarvis.core.Task taskInfo = getTaskInfo(priorityTask);
                    taskQueue.put(taskInfo);
                    runningTasks.incrementAndGet();
                }
                ThreadUtils.sleep(5000);
            }
        }
    }

    private Map<Long, DAGTask> readyTable = new ConcurrentHashMap<Long, DAGTask>();
    private final int INIT_PRIORITY_QUEUE_SIZE = 100;
    private Queue<DAGTask> runnableQueue =  new PriorityQueue<DAGTask>(
            INIT_PRIORITY_QUEUE_SIZE,
            new Comparator<DAGTask>() {
              @Override
              public int compare(DAGTask task1, DAGTask task2) {
                  return task2.getPriority() - task1.getPriority();
              }
          });
    private AtomicInteger runningTasks = new AtomicInteger(0);
    private int maxConcurrentNum = 70;

    // unique taskid
    private AtomicLong maxid = new AtomicLong(1);

    @Override
    public void handleInitEvent(InitEvent event) {
        scanThread = new ScanThread();
        scanThread.setName("ScanPriorityQueueThread");
        scanThread.start();

        List<Task> tasks = taskService.getTasksByStatus(JobStatus.READY);
        if (tasks != null) {
            for (Task task : tasks) {
                DAGTask dagTask = new DAGTask(task.getJobId(), task.getTaskId(), task.getAttemptId());
                readyTable.put(task.getTaskId(), dagTask);
                runnableQueue.offer(dagTask);
            }
        }
    }

    @Override
    public void handleStartEvent(StartEvent event) {
        if (scanThread != null && !scanThread.getState().equals(State.RUNNABLE)) {
            scanThread.start();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void handleStopEvent(StopEvent event) {
        if (scanThread != null && scanThread.isAlive()
                && scanThread.getState().equals(State.RUNNABLE)) {
            scanThread.stop();
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleSuccessEvent(SuccessEvent e) {
        updateJobStatus(e.getTaskId(), JobStatus.SUCCESS);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleRunningEvent(RunningEvent e) {
        updateJobStatus(e.getTaskId(), JobStatus.RUNNING);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleKilledEvent(KilledEvent e) {
        updateJobStatus(e.getTaskId(), JobStatus.KILLED);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleFailedEvent(FailedEvent e) {
        DAGTask dagTask = readyTable.get(e.getTaskId());
        if (dagTask != null) {
            int attemptId = dagTask.getAttemptId();
            if (attemptId <= dagTask.getMaxFailedAttempts()) {
                attemptId++;
                dagTask.setAttemptId(attemptId);
                ThreadUtils.sleep(dagTask.getFailedInterval());
                retryTask(dagTask);
            } else {
                updateJobStatus(e.getTaskId(), JobStatus.FAILED);
            }
        }
    }

    private void updateJobStatus(long taskId, JobStatus status) {
        // 1. store status to DB
        if (taskService != null) {
            if (status.equals(JobStatus.RUNNING)) {
                taskService.updateStatusWithStart(taskId, status);
            } else if (status.getValue() >= JobStatus.SUCCESS.getValue() &&
                    status.getValue() <= JobStatus.KILLED.getValue()) {
                taskService.updateStatusWithEnd(taskId, status);
            }
        }
        // 2. remove from readyTable
        DAGTask dagTask = readyTable.get(taskId);
        if (dagTask != null) {
            readyTable.remove(taskId);
            runningTasks.decrementAndGet();
        }
    }

    @VisibleForTesting
    public void clear() {
        readyTable.clear();
        runnableQueue.clear();
        maxid.set(1);
    }

    @VisibleForTesting
    public Map<Long, DAGTask> getReadyTable() {
        return readyTable;
    }

    public long submitJob(long jobId) {
        long taskId;
        if (jobMapper != null && taskMapper != null) {
            Task task = createNewTask(jobId);
            taskMapper.insert(task);
            taskId = task.getTaskId();
        } else {
            taskId = generateTaskId();
        }

        submitTask(new DAGTask(jobId, taskId));
        return taskId;
    }

    private void retryTask(DAGTask dagTask) {
        if (jobMapper != null && taskMapper != null) {
            Task task = updateTask(dagTask);
            taskMapper.updateByPrimaryKey(task);
        }

        submitTask(dagTask);
    }

    private void submitTask(DAGTask dagTask) {
        if (!readyTable.containsKey(dagTask.getTaskId())) {
            // add to readyTable and runnableQueue
            readyTable.put(dagTask.getTaskId(), dagTask);
            runnableQueue.offer(dagTask);
            if (jobMapper != null) {
                Job job = jobMapper.selectByPrimaryKey(dagTask.getJobId());
                dagTask.setPriority(job.getPriority());
                dagTask.setMaxFailedAttempts(job.getFailedAttempts());
                dagTask.setFailedInterval(job.getFailedInterval());
            }
        } else {
            // add to runnableQueue
            runnableQueue.offer(dagTask);
        }
    }

    private Task createNewTask(long jobId) {
        Task task = new Task();
        task.setJobId(jobId);
        task.setAttemptId(1);
        task.setExecuteUser(jobMapper.selectByPrimaryKey(jobId).getSubmitUser());
        task.setStatus(JobStatus.READY.getValue());
        Date currentTime = new Date();
        DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
        dateTimeFormat.format(currentTime);
        task.setCreateTime(currentTime);
        task.setUpdateTime(currentTime);

        return task;
    }

    private Task updateTask(DAGTask dagTask) {
        Task task = new Task();
        task.setTaskId(dagTask.getTaskId());
        task.setAttemptId(dagTask.getAttemptId());
        Date currentTime = new Date();
        DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
        dateTimeFormat.format(currentTime);
        task.setUpdateTime(currentTime);

        return task;
    }

    // 重启的时候maxid会重置, for testing
    private long generateTaskId() {
        return maxid.getAndIncrement();
    }

    private com.mogujie.jarvis.core.Task getTaskInfo(DAGTask dagTask) {
        String fullId = dagTask.getJobId() + "_" + dagTask.getTaskId() + "_" + dagTask.getAttemptId();
        Job job = jobMapper.selectByPrimaryKey(dagTask.getJobId());
        com.mogujie.jarvis.core.Task task = com.mogujie.jarvis.core.Task.newTaskBuilder()
                .setFullId(fullId)
                .setTaskName(job.getJobName())
                .setAppName(job.getAppName())
                .setUser(job.getSubmitUser())
                .setPriority(dagTask.getPriority())
                .setCommand(job.getContent())
                .setTaskType(job.getJobType())
                .setParameters(JsonHelper.parseJSON2Map(job.getParams()))
                .build();

        return task;
    }
}
