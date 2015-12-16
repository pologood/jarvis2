/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:33:52
 */

package com.mogujie.jarvis.server.scheduler.dag;

import com.mogujie.jarvis.core.domain.JobStatus;


/**
 * @author guangming
 *
 */
public abstract class AbstractDAGJob {

    protected JobStatus jobStatus = JobStatus.ENABLE;

    /**
     * return true if dependency check passed. Otherwise return false.
     */
    public abstract boolean checkDependency();

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }
}
