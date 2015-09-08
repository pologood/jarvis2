/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:49:39
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import java.util.Set;

import com.mogujie.jarvis.server.scheduler.dag.JobDependencyStrategy;

/**
 * @author guangming
 *
 */
public abstract class AbstractDependStatus {

    private long myjobId;

    /**
     * update ready dependency job status to true
     */
    public abstract void addReadyDependency(long jobId, long taskId);

    /**
     * update ready dependency job status to false
     */
    public abstract void removeReadyDependency(long jobId, long taskId);

    /**
     * remove job dependency
     */
    public abstract void removeDependency(long jobId);

    /**
     * return true if finished all jobs
     */
    public abstract boolean isFinishAllJob(JobDependencyStrategy strategy, Set<Long> needJobs);

    /**
     * reset dependency status
     */
    public abstract void reset();

    public void setMyjobId(long jobId) {
        this.myjobId = jobId;
    }

}
