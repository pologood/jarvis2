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

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.common.util.ThreadUtils;
import com.mogujie.jarvis.dao.JobMapper;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.server.observer.InitEvent;
import com.mogujie.jarvis.server.observer.StopEvent;
import com.mogujie.jarvis.server.scheduler.Scheduler;
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
    private long maxid = 1;

    @Override
    public void handleInitEvent(InitEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleStopEvent(StopEvent event) {
        // TODO Auto-generated method stub

    }

    public long submitJob(long jobid) {
        // TODO
        // 1. generate a new taskid
        long taskid = generateTaskId();
        readyTable.put(taskid, new DAGTask(jobid, taskid, 0));

        // 2. submit job
        Job job = jobMapper.selectByPrimaryKey((int)jobid);
        // submit jobcontext

        return taskid;
    }

    public void submitJob(long jobid, long taskid) {
        // TODO submitjob
    }

    @Subscribe
    public void handleSuccessEvent(SuccessEvent e) {
        // TODO 1. store success status to DB

        // 2. remove from ready table
        readyTable.remove(e.getTaskid());
    }

    @Subscribe
    public void handleFailedEvent(FailedEvent e) {
        long jobid = e.getJobid();
        long taskid = e.getTaskid();
        DAGTask dagTask = readyTable.get(e.getTaskid());
        if (dagTask != null) {
            Job job = jobMapper.selectByPrimaryKey((int)jobid);
            int failedTimes = dagTask.getFailedTimes();
            if (failedTimes < job.getFailedAttempts()) {
                failedTimes++;
                dagTask.setFailedTimes(failedTimes);
                ThreadUtils.sleep(job.getFailedInterval());
                submitJob(jobid, taskid);
            } else {
                // TODO 1. store success status to DB

                // 2. remove from ready table
                readyTable.remove(taskid);
            }
        }
    }

    private long generateTaskId() {
        return ++maxid;
    }

}
