/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:46:32
 */

package com.mogujie.jarvis.server.scheduler.task;

import com.mogujie.jarvis.server.domain.JobKey;

/**
 * @author guangming
 *
 */
public class DAGTask {
    private JobKey jobKey;
    private long taskId;
    private int attemptId;

    public DAGTask(JobKey jobKey, long taskId) {
        this(jobKey, taskId, 1);
    }

    public DAGTask(JobKey jobKey, long taskId, int attemptId) {
        this.jobKey = jobKey;
        this.taskId = taskId;
        this.attemptId = attemptId;
    }

    public JobKey getJobKey() {
        return jobKey;
    }

    public void setJobKey(JobKey jobKey) {
        this.jobKey = jobKey;
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
        return "[jobKey=" + jobKey +
                ", taskId=" + taskId +
                ", attemptId=" + attemptId + "]";
    }
}
