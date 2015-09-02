/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月1日 下午1:58:37
 */

package com.mogujie.jarvis.server.scheduler.dag.event;

import com.mogujie.jarvis.server.observer.Event;


/**
 * @author guangming
 *
 */
public abstract class DAGJobEvent implements Event {
    private long jobid;

    public DAGJobEvent(long jobid) {
        this.jobid = jobid;
    }

    public long getJobid() {
        return jobid;
    }

    public void setJobid(long jobid) {
        this.jobid = jobid;
    }
}
