/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月18日 下午5:12:39
 */

package com.mogujie.jarvis.server.scheduler.event;

/**
 * @author guangming
 *
 */
public class RetryTaskEvent extends DAGTaskEvent{
    private boolean runChild;

    /**
     * @param jobId
     * @param taskId
     */
    public RetryTaskEvent(long jobId, long taskId) {
        super(jobId, taskId);
    }

    public RetryTaskEvent(long jobId, long taskId, boolean runChild) {
        this(jobId, taskId);
        this.runChild = runChild;
    }

    public boolean isRunChild() {
        return runChild;
    }

    public void setRunChild(boolean runChild) {
        this.runChild = runChild;
    }
}
