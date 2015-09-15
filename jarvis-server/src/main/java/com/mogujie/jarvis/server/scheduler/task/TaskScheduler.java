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
import com.mogujie.jarvis.core.common.util.ThreadUtils;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dao.TaskMapper;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.Task;
import com.mogujie.jarvis.server.scheduler.InitEvent;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.StartEvent;
import com.mogujie.jarvis.server.scheduler.StopEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.SuccessEvent;
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
        private String name;

        public ScanThread(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            while (true) {
                while (!runnableQueue.isEmpty() && runningTasks.get() < maxConcurrentNum) {
                    DAGTask priorityTask = runnableQueue.poll();
                    //TODO submit priorityTask
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
        scanThread = new ScanThread("ScanPriorityQueueThread");
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
        long taskId = e.getTaskId();
        // 1. store success status to DB
        taskService.updateStatus(taskId, JobStatus.SUCCESS);
        // 2. remove from readyTable
        DAGTask dagTask = readyTable.get(taskId);
        if (dagTask != null) {
            readyTable.remove(taskId);
            runningTasks.decrementAndGet();
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleFailedEvent(FailedEvent e) {
        long taskId = e.getTaskId();
        DAGTask dagTask = readyTable.get(e.getTaskId());
        if (dagTask != null) {
            int attemptId = dagTask.getAttemptId();
            if (attemptId <= dagTask.getMaxFailedAttempts()) {
                attemptId++;
                dagTask.setAttemptId(attemptId);
                ThreadUtils.sleep(dagTask.getFailedInterval());
                retryTask(dagTask);
            } else {
                // 1. store failed status to DB
                if (taskService != null) {
                    taskService.updateStatus(taskId, JobStatus.FAILED);
                }
                // 2. remove from readyTable
                readyTable.remove(taskId);
                runningTasks.decrementAndGet();
            }
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
}
