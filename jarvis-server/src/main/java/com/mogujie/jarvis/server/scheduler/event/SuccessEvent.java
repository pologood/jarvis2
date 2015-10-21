/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月1日 上午11:53:44
 */

package com.mogujie.jarvis.server.scheduler.event;

import com.mogujie.jarvis.server.domain.JobKey;

/**
 * @author guangming
 *
 */
public class SuccessEvent extends DAGTaskEvent {

    /**
     * @param jobId
     * @param taskId
     */
    public SuccessEvent(JobKey jobKey, long taskId) {
        super(jobKey, taskId);
    }

}
