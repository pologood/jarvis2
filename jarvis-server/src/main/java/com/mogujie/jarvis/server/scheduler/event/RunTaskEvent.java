/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月23日 下午3:19:30
 */

package com.mogujie.jarvis.server.scheduler.event;

/**
 * This Event handled by {@link com.mogujie.jarvis.server.scheduler.task.TaskScheduler}.
 * Sent by {@link com.mogujie.jarvis.server.scheduler.time.TimeScheduler}.
 *
 * 用来触发任务执行，触发子任务的调度。未来可扩展作为维护手段，强制执行某个任务。
 *
 * @author guangming
 *
 */
public class RunTaskEvent extends DAGTaskEvent {

    /**
     * @param jobId
     * @param taskId
     */
    public RunTaskEvent(long jobId, long taskId) {
        super(jobId, taskId);
    }

}
