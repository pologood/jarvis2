/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月2日 上午11:54:45
 */

package com.mogujie.jarvis.server.scheduler.dag.event;

import com.mogujie.jarvis.server.scheduler.JobDescriptor;

/**
 * @author guangming
 *
 */
public class AddJobEvent extends DAGJobEvent{
    private JobDescriptor jobDesc;

    /**
     * @param long jobId
     * @param JobDescriptor jobDesc
     */
    public AddJobEvent(long jobId, JobDescriptor jobDesc) {
        super(jobId);
    }

    public JobDescriptor getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(JobDescriptor jobDesc) {
        this.jobDesc = jobDesc;
    }
}
