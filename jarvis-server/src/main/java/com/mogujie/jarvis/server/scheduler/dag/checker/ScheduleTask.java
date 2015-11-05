/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月2日 下午8:14:19
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

/**
 * @author guangming
 *
 */
public class ScheduleTask {
    long taskId;
    long scheduleTime;
    boolean status;

    public ScheduleTask(long taskId, long scheduleTime) {
        this.taskId = taskId;
        this.scheduleTime = scheduleTime;
        this.status = false;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(long scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
