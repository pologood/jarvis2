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

import com.mogujie.jarvis.core.JobContext;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.dag.DAGScheduleException;
import com.mogujie.jarvis.server.scheduler.dag.event.DAGEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.dag.event.SuccessEvent;

/**
 * Scheduler used to handle running tasks.
 *
 * @author guangming
 *
 */
public enum TaskScheduler {
    INSTANCE;

    // TODO 优化：按照任务优先级排序，使用优先级队列或者堆？
    private Map<Long, DAGTask> readyTable = new ConcurrentHashMap<Long, DAGTask>();
    private long maxid = 1;

    public void init() {
        // TODO Auto-generated method stub

    }

    public void run() {
        // TODO Auto-generated method stub

    }

    public void stop() {
        // TODO Auto-generated method stub

    }

    public long submitJob(long jobid) {
        // TODO
        // 1. generate a new taskid
        long taskid = generateTaskId();
        readyTable.put(taskid, new DAGTask(jobid, taskid, 0));

        // 2. submit job
        JobContext jobContext = SchedulerUtil.getJobContext(jobid);
        // submit jobcontext

        return taskid;
    }

    public void submitJob(long jobid, long taskid) {
        // TODO submitjob
    }

    public void handleEvent(DAGEvent e) throws DAGScheduleException {
        if (e instanceof SuccessEvent) {
            handleSuccessEvent((SuccessEvent)e);
        } else if (e instanceof FailedEvent) {
            handleFailedEvent((FailedEvent)e);
        }
    }

    private void handleSuccessEvent(SuccessEvent e) {
        // TODO 1. store success status to DB

        // 2. remove from ready table
        readyTable.remove(e.getTaskid());
    }

    private void handleFailedEvent(FailedEvent e) {
        DAGTask dagTask = readyTable.get(e.getTaskid());
        int failedTimes = dagTask.getFailedTimes();
        if (failedTimes < e.getFailedRetries()) {
            failedTimes++;
            dagTask.setFailedTimes(failedTimes);
            submitJob(e.getJobid(), e.getTaskid());
        } else {
            // TODO 1. store success status to DB

            // 2. remove from ready table
            readyTable.remove(e.getTaskid());
        }
    }

    private long generateTaskId() {
        return ++maxid;
    }

}
