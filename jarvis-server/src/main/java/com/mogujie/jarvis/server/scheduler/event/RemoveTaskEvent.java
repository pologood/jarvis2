/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月16日 上午11:24:06
 */

package com.mogujie.jarvis.server.scheduler.event;

/**
 * This Event handled by {@link com.mogujie.jarvis.server.scheduler.task.TaskScheduler}.
 * Sent by {@link com.mogujie.jarvis.server.actor.TaskActor}
 *
 * @author guangming
 *
 */
public class RemoveTaskEvent extends DAGTaskEvent {

    /**
     * @param jobId
     * @param taskId
     */
    public RemoveTaskEvent(long jobId, long taskId) {
        super(jobId, taskId);
    }

}
