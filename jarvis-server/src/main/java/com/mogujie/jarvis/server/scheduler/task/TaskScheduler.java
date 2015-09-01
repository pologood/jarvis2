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

/**
 * Scheduler used to handle running tasks.
 *
 * @author guangming
 *
 */
public enum TaskScheduler {
    INSTANCE;

    private Map<Integer, DAGTask> runningTable = new ConcurrentHashMap<Integer, DAGTask>();

    public void init() {
        // TODO Auto-generated method stub

    }

    public void run() {
        // TODO Auto-generated method stub

    }

    public void stop() {
        // TODO Auto-generated method stub

    }

    public boolean submitJob(JobContext jobContext) {
        return false;
    }

}
