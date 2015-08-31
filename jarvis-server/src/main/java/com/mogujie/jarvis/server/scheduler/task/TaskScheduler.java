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

import com.mogujie.jarvis.server.scheduler.AbstractScheduler;

/**
 * Scheduler used to handle running tasks.
 *
 * @author guangming
 *
 */
public class TaskScheduler extends AbstractScheduler {

    private Map<Integer, DAGTask> runningTable = new ConcurrentHashMap<Integer, DAGTask>();

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

}
