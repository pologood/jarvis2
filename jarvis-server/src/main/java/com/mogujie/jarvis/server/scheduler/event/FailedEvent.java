/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月1日 下午2:02:12
 */

package com.mogujie.jarvis.server.scheduler.event;

import com.mogujie.jarvis.server.domain.JobKey;


/**
 * @author guangming
 *
 */
public class FailedEvent extends DAGTaskEvent {

    /**
     * @param  jobId
     * @param  taskId
     */
    public FailedEvent(JobKey jobKey, long taskId) {
        super(jobKey, taskId);
    }

}
