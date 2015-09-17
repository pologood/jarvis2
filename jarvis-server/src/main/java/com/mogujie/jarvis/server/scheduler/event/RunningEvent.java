/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月16日 下午4:16:43
 */

package com.mogujie.jarvis.server.scheduler.event;

/**
 * @author guangming
 *
 */
public class RunningEvent extends DAGTaskEvent {

    /**
     * @param jobId
     * @param taskId
     */
    public RunningEvent(long jobId, long taskId) {
        super(jobId, taskId);
    }

}
