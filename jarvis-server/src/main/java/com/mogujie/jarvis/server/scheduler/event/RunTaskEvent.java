/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月23日 下午3:19:30
 */

package com.mogujie.jarvis.server.scheduler.event;

/**
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
