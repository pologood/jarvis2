/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年10月13日 下午7:35:20
 */

package com.mogujie.jarvis.server.actor;

/**
 * Rollback for testing
 *
 * @author guangming
 *
 */
public class RemoveJobRequest {
    private long jobId;

    public RemoveJobRequest(long jobId) {
        this.jobId = jobId;
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }
}
