/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:50:42
 */

package com.mogujie.jarvis.server.scheduler.task;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        // TODO 1. store success status to DB

        // 2. remove from ready table
        readyTable.remove(e.getTaskId());
    }

    @Subscribe
    public void handleFailedEvent(FailedEvent e) {
        long jobId = e.getJobId();
        long taskId = e.getTaskId();
        DAGTask dagTask = readyTable.get(e.getTaskId());
        if (dagTask != null) {
            Job job = jobMapper.selectByPrimaryKey(jobId);
            int failedTimes = dagTask.getFailedTimes();
            if (failedTimes < job.getFailedAttempts()) {
                failedTimes++;
                dagTask.setFailedTimes(failedTimes);
                ThreadUtils.sleep(job.getFailedInterval());
                submitTask(jobId, taskId);
            } else {
                // TODO 1. store success status to DB

                // 2. remove from ready table
                readyTable.remove(taskId);
            }
        }
    }

    public long submitJob(long jobId) {
        long taskId = generateTaskId();
        submitTask(jobId, taskId);

        return taskId;
    }

    private void submitTask(long jobId, long taskId) {
        // 1. insert new task to DB
        Task task = createNewTask(jobId, taskId);
        taskMapper.insert(task);

        // 2. add to readyTable
        readyTable.put(taskId, new DAGTask(jobId, taskId, 1));
    }

    private Task createNewTask(long jobId, long taskId) {
        Job job = jobMapper.selectByPrimaryKey(jobId);
        Task task = new Task();
        task.setJobId(jobId);
        task.setTaskId(taskId);
        task.setAttemptId(readyTable.get(taskId).getFailedTimes());
        task.setAttemptInfo("init task");
        task.setExecutUser(job.getExecutUser());
        // TODO other set
//        task.setStatus(JobStatus.READY.getValue());

        return task;
    }

    private long generateTaskId() {
        return maxid.incrementAndGet();
    }

}
