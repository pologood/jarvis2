/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:50:42
 */

package com.mogujie.jarvis.server.scheduler.task;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.common.util.ThreadUtils;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dao.TaskMapper;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.Task;
import com.mogujie.jarvis.server.scheduler.InitEvent;
import com.mogujie.jarvis.server.scheduler.Scheduler;
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

    // TODO 优化：按照任务优先级排序，使用优先级队列或者堆？
    private Map<Long, DAGTask> readyTable = new ConcurrentHashMap<Long, DAGTask>();
    private AtomicLong maxid = new AtomicLong(1);
    // 在这里做并发度控制？
    private int concurrentNum = 70;

    @Override
    public void handleInitEvent(InitEvent event) {
        List<Task> tasks = taskService.getTasksByStatus(JobStatus.READY);
        if (tasks != null) {
            for (Task task : tasks) {
                DAGTask dagTask = new DAGTask(task.getJobId(), task.getTaskId(), task.getAttemptId());
                readyTable.put(task.getTaskId(), dagTask);
            }
        }
    }

    @Override
    public void handleStopEvent(StopEvent event) {
        concurrentNum = 0;
    }

    @Subscribe
    public void handleSuccessEvent(SuccessEvent e) {
        long taskId = e.getTaskId();
        // 1. store success status to DB
        taskService.updateStatus(taskId, JobStatus.SUCCESS);
        // 2. remove from ready table
        readyTable.remove(taskId);
    }

    @Subscribe
    public void handleFailedEvent(FailedEvent e) {
        long taskId = e.getTaskId();
        DAGTask dagTask = readyTable.get(e.getTaskId());
        if (dagTask != null) {
            int attemptId = dagTask.getAttempId();
            if (attemptId <= dagTask.getMaxFailedAttempts()) {
                attemptId++;
                dagTask.setAttemptId(attemptId);
                ThreadUtils.sleep(dagTask.getFailedInterval());
                submitTask(dagTask);
            } else {
                // 1. store failed status to DB
                if (taskService != null) {
                    taskService.updateStatus(taskId, JobStatus.FAILED);
                }
                // 2. remove from ready table
                readyTable.remove(taskId);
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
        long taskId = generateTaskId();
        submitTask(new DAGTask(jobId, taskId));
        return taskId;
    }

    private void submitTask(DAGTask dagTask) {
        // 1. insert new task to DB
        if (jobMapper != null && taskMapper != null) {
            Task task = createNewTask(dagTask.getJobId(), dagTask.getTaskId(), dagTask.getAttempId());
            taskMapper.insert(task);
        }

        // 2. add to readyTable
        if (!readyTable.containsKey(dagTask.getTaskId())) {
            readyTable.put(dagTask.getTaskId(), dagTask);
            if (jobMapper != null) {
                Job job = jobMapper.selectByPrimaryKey(dagTask.getJobId());
                dagTask.setMaxFailedAttempts(job.getFailedAttempts());
                dagTask.setFailedInterval(job.getFailedInterval());
            }
        }
    }

    private Task createNewTask(long jobId, long taskId, int attemptId) {
        Task task = new Task();
        task.setJobId(jobId);
        task.setTaskId(taskId);
        task.setAttemptId(attemptId);
        task.setExecuteUser(jobMapper.selectByPrimaryKey(jobId).getSubmitUser());
        task.setStatus(JobStatus.READY.getValue());
        Date currentTime = new Date();
        DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
        dateTimeFormat.format(currentTime);
        task.setCreateTime(currentTime);
        task.setUpdateTime(currentTime);

        return task;
    }

    // TODO 重启的时候maxid会重置
    private long generateTaskId() {
        return maxid.getAndIncrement();
    }
}
