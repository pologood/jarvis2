/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月1日 上午11:53:44
 */

package com.mogujie.jarvis.server.scheduler.event;

/**
 * @author guangming
 *
 */
public class SuccessEvent extends DAGTaskEvent {
    /**
     * @param jobId
     * @param taskId
     */
    public SuccessEvent(long jobId, long taskId) {
        super(jobId, taskId);
    }

    /**
     * @param taskId
     */
    public SuccessEvent(long taskId) {
        super(taskId);
    }
}
