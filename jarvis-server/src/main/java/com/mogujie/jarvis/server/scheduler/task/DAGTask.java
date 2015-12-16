/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:46:32
 */

package com.mogujie.jarvis.server.scheduler.task;

import java.util.List;
import java.util.Map;

import com.mogujie.jarvis.server.scheduler.task.checker.TaskStatusChecker;

/**
 * @author guangming
 *
 */
public class DAGTask {
    private long jobId;
    private long taskId;
    private int attemptId;
    private long scheduleTime;
    private TaskStatusChecker statusChecker;

    public DAGTask(long jobId, long taskId, int attemptId, long scheduleTime) {
        this.jobId = jobId;
        this.taskId = taskId;
        this.attemptId = attemptId;
        this.scheduleTime = scheduleTime;
        this.statusChecker = new TaskStatusChecker(jobId, taskId, scheduleTime);
    }

    public DAGTask(long jobId, long taskId, long scheduleTime, Map<Long, List<Long>> dependTaskIdMap) {
        this(jobId, taskId, 1, scheduleTime, dependTaskIdMap);
    }

    public DAGTask(long jobId, long taskId, int attemptId, long scheduleTime, Map<Long, List<Long>> dependTaskIdMap) {
        this.jobId = jobId;
        this.taskId = taskId;
        this.attemptId = attemptId;
        this.scheduleTime = scheduleTime;
        this.statusChecker = new TaskStatusChecker(jobId, taskId, scheduleTime, dependTaskIdMap);
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

    public long getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(long scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public TaskStatusChecker getStatusChecker() {
        return statusChecker;
    }

    public void setStatusChecker(TaskStatusChecker statusChecker) {
        this.statusChecker = statusChecker;
    }

    public boolean checkStatus() {
        long timeStamp = System.currentTimeMillis();
        long scheduleTime = getScheduleTime();
        return (scheduleTime <= timeStamp) && statusChecker.checkStatus();
    }

    public List<Long> getDependTaskIds() {
        return statusChecker.getDependTaskIds();
    }

    @Override
    public String toString() {
        return "DAGTask [jobId=" + jobId + ", taskId=" + taskId + ", attemptId=" + attemptId + ", scheduleTime=" + scheduleTime + "]";
    }
}
