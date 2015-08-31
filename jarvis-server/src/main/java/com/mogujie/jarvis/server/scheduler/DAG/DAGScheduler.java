/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:50:07
 */

package com.mogujie.jarvis.server.scheduler.DAG;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mogujie.jarvis.server.scheduler.AbstractScheduler;
import com.mogujie.jarvis.server.scheduler.DAG.job.DAGJob;

/**
 * Scheduler used to handle dependency based job.
 *
 * @author guangming
 *
 */
public class DAGScheduler extends AbstractScheduler {

    private Map<Integer, DAGJob> waitingTable = new ConcurrentHashMap<Integer, DAGJob>();

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
