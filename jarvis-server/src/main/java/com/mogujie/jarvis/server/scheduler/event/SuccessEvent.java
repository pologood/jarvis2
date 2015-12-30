/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月1日 上午11:53:44
 */

package com.mogujie.jarvis.server.scheduler.event;

/**
 * This Event handled by {@link com.mogujie.jarvis.server.scheduler.task.TaskScheduler}.
 * Sent by {@link com.mogujie.jarvis.server.actor.TaskMetricsActor}.
 *
 * 用来处理任务成功的事件
 *
 * @author guangming
 *
 */
public class SuccessEvent extends DAGTaskEvent {
    private long scheduleTime;
    private boolean isTemp;

    /**
     * @param jobId
     * @param taskId
     * @param scheduleTime
     */
    public SuccessEvent(long jobId, long taskId, long scheduleTime) {
        super(jobId, taskId);
        this.scheduleTime = scheduleTime;
    }

    /**
     * @param jobId
     * @param taskId
     * @param scheduleTime
     * @param isTemp
     */
    public SuccessEvent(long jobId, long taskId, long scheduleTime, boolean isTemp) {
        super(jobId, taskId);
        this.scheduleTime = scheduleTime;
        this.isTemp = isTemp;
    }

    public long getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(long scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean isTemp) {
        this.isTemp = isTemp;
    }

}
