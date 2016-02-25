/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年2月25日 下午5:44:58
 */

package com.mogujie.jarvis.server.scheduler.event;

import com.mogujie.jarvis.core.domain.TaskType;

/**
 * This Event handled by {@link com.mogujie.jarvis.server.scheduler.task.TaskScheduler}.
 * Sent by {@link com.mogujie.jarvis.server.actor.TaskMetricsActor}.
 *
 * 用来处理删除task的事件
 *
 * @author guangming
 *
 */
public class RemoveTaskEvent extends DAGTaskEvent {
    private long scheduleTime;
    private TaskType taskType;

    /**
     * @param jobId
     * @param taskId
     * @param scheduleTime
     * @param taskType
     * @param reason
     */
    public RemoveTaskEvent(long jobId, long taskId, long scheduleTime, TaskType taskType) {
        super(jobId, taskId);
        this.scheduleTime = scheduleTime;
        this.taskType = taskType;
    }

    public long getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(long scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

}
