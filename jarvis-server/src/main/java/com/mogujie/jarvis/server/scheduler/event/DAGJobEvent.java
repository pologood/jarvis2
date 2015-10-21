/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月1日 下午1:58:37
 */

package com.mogujie.jarvis.server.scheduler.event;

import com.mogujie.jarvis.core.observer.Event;
import com.mogujie.jarvis.server.domain.JobKey;


/**
 * @author guangming
 *
 */
public abstract class DAGJobEvent implements Event {
    private JobKey jobKey;

    public DAGJobEvent(JobKey jobKey) {
        this.jobKey = jobKey;
    }

    public JobKey getJobKey() {
        return jobKey;
    }

    public void setJobKey(JobKey jobKey) {
        this.jobKey = jobKey;
    }
}
