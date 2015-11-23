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

}
