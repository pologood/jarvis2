/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月1日 下午2:02:12
 */

package com.mogujie.jarvis.server.scheduler.dag.event;


/**
 * @author guangming
 *
 */
public class FailedEvent extends DAGTaskEvent {
    private int failedRetries;
    private int failedInterval;

    /**
     * @param long jobid
     * @param long taskid
     * @param int failedRetries
     * @param int failedInterval
     */
    public FailedEvent(long jobid, long taskid, int failedRetries, int failedInterval) {
        super(jobid, taskid);
        this.failedRetries = failedRetries;
        this.failedInterval = failedInterval;
    }

    public int getFailedRetries() {
        return failedRetries;
    }

    public void setFailedRetries(int failedRetries) {
        this.failedRetries = failedRetries;
    }

    public int getFailedInterval() {
        return failedInterval;
    }

    public void setFailedInterval(int failedInterval) {
        this.failedInterval = failedInterval;
    }
}
