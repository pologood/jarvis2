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
    private int attemptId;

    public DAGTask(long jobId, long taskId) {
        this(jobId, taskId, 1);
    }

    public DAGTask(long jobId, long taskId, int attemptId) {
        this.jobId = jobId;
        this.taskId = taskId;
        this.attemptId = attemptId;
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

    public int getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }


    @Override
    public String toString() {
        return "[jobId=" + jobId +
                ", taskId=" + taskId +
                ", attemptId=" + attemptId + "]";
    }
}
