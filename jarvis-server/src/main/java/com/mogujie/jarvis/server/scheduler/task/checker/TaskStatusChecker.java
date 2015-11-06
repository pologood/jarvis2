/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月4日 下午7:14:59
 */

package com.mogujie.jarvis.server.scheduler.task.checker;


/**
 * @author guangming
 *
 */
public abstract class TaskStatusChecker {

    private long myJobId;
    private long myTaskId;

    public TaskStatusChecker(long jobId, long taskId) {
        this.myJobId = jobId;
        this.myTaskId = taskId;
    }

    public long getMyJobId() {
        return myJobId;
    }

    public void setMyJobId(long myJobId) {
        this.myJobId = myJobId;
    }

    public long getMyTaskId() {
        return myTaskId;
    }

    public void setMyTaskId(long myTaskId) {
        this.myTaskId = myTaskId;
    }

    public abstract boolean checkStatus();
}
