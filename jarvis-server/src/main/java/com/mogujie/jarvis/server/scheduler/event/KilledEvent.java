/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月16日 下午4:17:46
 */

package com.mogujie.jarvis.server.scheduler.event;

import com.mogujie.jarvis.server.domain.JobKey;

/**
 * @author guangming
 *
 */
public class KilledEvent extends DAGTaskEvent {

    /**
     * @param jobId
     * @param taskId
     */
    public KilledEvent(JobKey jobKey, long taskId) {
        super(jobKey, taskId);
    }

}
