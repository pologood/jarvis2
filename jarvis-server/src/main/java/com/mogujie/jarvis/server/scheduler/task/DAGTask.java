/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:46:32
 */

package com.mogujie.jarvis.server.scheduler.task;

/**
 * @author guangming
 *
 */
public class DAGTask {
    private long jobId;
    private long taskId;
    private int failedTimes;

    public DAGTask(long jobId, long taskId, int failedTimes) {
        this.jobId = jobId;
        this.taskId = taskId;
        this.failedTimes = failedTimes;
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public int getFailedTimes() {
        return failedTimes;
    }

    public void setFailedTimes(int failedTimes) {
        this.failedTimes = failedTimes;
    }
}
