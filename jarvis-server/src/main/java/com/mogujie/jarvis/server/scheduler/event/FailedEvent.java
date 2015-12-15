/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月1日 下午2:02:12
 */

package com.mogujie.jarvis.server.scheduler.event;


/**
 * This Event handled by {@link com.mogujie.jarvis.server.scheduler.task.TaskScheduler}.
 * Sent by {@link com.mogujie.jarvis.server.actor.TaskMetricsActor}.
 *
 * 用来处理任务失败的事件
 *
 * @author guangming
 *
 */
public class FailedEvent extends DAGTaskEvent {

    /**
     * @param  jobId
     * @param  taskId
     */
    public FailedEvent(long jobId, long taskId) {
        super(jobId, taskId);
    }

    /**
     * @param  taskId
     */
    public FailedEvent(long taskId) {
        super(taskId);
    }

}
