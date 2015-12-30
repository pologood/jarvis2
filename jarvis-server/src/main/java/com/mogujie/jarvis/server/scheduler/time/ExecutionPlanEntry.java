/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年11月23日 下午2:35:51
 */

package com.mogujie.jarvis.server.scheduler.time;

import java.util.Objects;

import org.joda.time.DateTime;

public class ExecutionPlanEntry {
    private final long jobId;
    private final DateTime dateTime;
    private long taskId;

    public ExecutionPlanEntry(long jobId, DateTime dateTime) {
        this.jobId = jobId;
        this.dateTime = dateTime;
    }

    public ExecutionPlanEntry(long jobId, DateTime dateTime, long taskId) {
        this.jobId = jobId;
        this.dateTime = dateTime;
        this.taskId = taskId;
    }

    public long getJobId() {
        return jobId;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId, dateTime);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ExecutionPlanEntry)) {
            return false;
        }

        ExecutionPlanEntry other = (ExecutionPlanEntry) obj;
        if (taskId > 0 && other.getTaskId() > 0 && taskId == other.getTaskId()) {
            return true;
        } else if (jobId == other.getJobId() && dateTime.isEqual(other.getDateTime())) {
            return true;
        } else {
            return false;
        }
    }

}
