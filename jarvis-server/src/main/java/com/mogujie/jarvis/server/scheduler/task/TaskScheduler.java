/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:50:42
 */

package com.mogujie.jarvis.server.scheduler.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.common.util.ThreadUtils;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.server.scheduler.InitEvent;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.StopEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.SuccessEvent;

/**
 * Scheduler used to handle ready tasks.
 *
 * @author guangming
 *
 */
public class TaskScheduler implements Scheduler {

    @Autowired
    JobMapper jobMapper;

    private static TaskScheduler instance = new TaskScheduler();
    private TaskScheduler() {}
    public static TaskScheduler getInstance() {
        return instance;
    }

    // TODO 优化：按照任务优先级排序，使用优先级队列或者堆？
    private Map<Long, DAGTask> readyTable = new ConcurrentHashMap<Long, DAGTask>();
    private AtomicLong maxid = new AtomicLong(1);

    @Override
    public void handleInitEvent(InitEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleStopEvent(StopEvent event) {
        // TODO Auto-generated method stub

    }

    public long submitJob(long jobId) {
        // TODO
        // 1. generate a new taskId
        long taskId = generateTaskId();
        readyTable.put(taskId, new DAGTask(jobId, taskId, 0));

        // 2. submit job
        Job job = jobMapper.selectByPrimaryKey(jobId);
        // submit jobcontext

        return taskId;
    }

    public void submitJob(long jobId, long taskId) {
        // TODO submitjob
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
                submitJob(jobId, taskId);
            } else {
                // TODO 1. store success status to DB

                // 2. remove from ready table
                readyTable.remove(taskId);
            }
        }
    }

    private long generateTaskId() {
        return maxid.incrementAndGet();
    }

}
