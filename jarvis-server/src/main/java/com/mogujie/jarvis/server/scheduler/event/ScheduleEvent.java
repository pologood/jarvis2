/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年10月30日 上午11:04:59
 */

package com.mogujie.jarvis.server.scheduler.event;

/**
 * @author guangming
 *
 */
public class ScheduleEvent extends DAGTaskEvent {
    private long scheduleTime;

    public ScheduleEvent(long jobId, long taskId, long scheduleTime) {
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
