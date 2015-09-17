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
    private int maxFailedAttempts;
    private int failedInterval;
    private int priority;
    private final static int DEFAULT_PRIORITY = 3;

    public DAGTask(long jobId, long taskId) {
        this(jobId, taskId, DEFAULT_PRIORITY);
    }

    public DAGTask(long jobId, long taskId, int priority) {
        this(jobId, taskId, 1, priority);
    }

    public DAGTask(long jobId, long taskId, int attemptId, int priority) {
        this.jobId = jobId;
        this.taskId = taskId;
        this.attemptId = attemptId;
        this.priority = priority;
        this.maxFailedAttempts = 3;
        this.failedInterval = 1000;
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

    public int getMaxFailedAttempts() {
        return maxFailedAttempts;
    }

    public void setMaxFailedAttempts(int maxFailedAttempts) {
        this.maxFailedAttempts = maxFailedAttempts;
    }

    public int getFailedInterval() {
        return failedInterval;
    }

    public void setFailedInterval(int failedInterval) {
        this.failedInterval = failedInterval;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "[jobId=" + jobId +
                ", taskId=" + taskId +
                ", attemptId=" + attemptId +
                ", priority=" + priority +
                ", maxFailedAttempts=" + maxFailedAttempts +
                ", failedInterval=" + failedInterval + "ms]";
    }
}
