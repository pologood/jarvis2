/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年10月20日 下午8:01:52
 */

package com.mogujie.jarvis.server.domain;

import org.joda.time.DateTime;

/**
 * @author guangming
 *
 */
public class JobKey {
    private long jobId;
    private long version;

    public JobKey(long jobId) {
        this.jobId = jobId;
        DateTime dt = DateTime.now();
        this.version = Long.parseLong(dt.toString("yyyyMMdd"));
    }

    public JobKey(long jobId, long version) {
        this.jobId = jobId;
        this.version = version;
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return jobId + "_" + version;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null) {
            return false;
        }

        if (getClass() != other.getClass()) {
            return false;
        }

        JobKey otherKey = (JobKey) other;
        if (jobId == otherKey.getJobId() && version == otherKey.getVersion()) {
            return true;
        } else {
            return false;
        }
    }
}
