/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月5日 下午7:44:51
 */

package com.mogujie.jarvis.server.scheduler.event;

/**
 * @author guangming
 *
 */
public class AddTaskEvent extends DAGTaskEvent {
    private long scheduleTime;

    /**
     * @param jobId
     * @param taskId
     * @param scheduleTime
     */
    public AddTaskEvent(long jobId, long taskId, long scheduleTime) {
        super(jobId, taskId);
        this.scheduleTime = scheduleTime;
    }

    public long getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(long scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

}
