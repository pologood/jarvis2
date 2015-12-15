/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月1日 下午1:55:51
 */

package com.mogujie.jarvis.server.scheduler.event;

import org.joda.time.DateTime;

/**
 * This Event handled by {@link com.mogujie.jarvis.server.scheduler.dag.DAGScheduler}.
 * Sent by {@link com.mogujie.jarvis.server.scheduler.time.TimeScheduler}.
 *
 * 处理time based job时间到达的事件
 *
 * @author guangming
 *
 */
public class TimeReadyEvent extends DAGJobEvent {
    private long scheduleTime;

    /**
     * @param jobId
     */
    public TimeReadyEvent(long jobId) {
        super(jobId);
        scheduleTime = DateTime.now().getMillis();
    }

    /**
     * @param jobId
     * @param scheduleTime
     */
    public TimeReadyEvent(long jobId, long scheduleTime) {
        super(jobId);
        this.scheduleTime = scheduleTime;
    }

    public long getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(long scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    @Override
    public String toString() {
        return "TimeReadyEvent [jobId=" + getJobId() + ", scheduleTime=" + scheduleTime + "]";
    }

}
