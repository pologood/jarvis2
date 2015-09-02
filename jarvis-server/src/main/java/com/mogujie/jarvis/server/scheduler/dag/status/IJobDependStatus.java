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
public abstract class IJobDependStatus {

    private long myjobid;

    /**
     * update ready dependency job status to true
     */
    public abstract void addReadyDependency(long jobid, long taskid);

    /**
     * update ready dependency job status to false
     */
    public abstract void removeReadyDependency(long jobid, long taskid);

    /**
     * remove job dependency
     */
    public abstract void removeDependency(long jobid);

    /**
     * return true if finished all jobs
     */
    public abstract boolean isFinishAllJob(JobDependencyStrategy strategy, Set<Long> needJobs);

    /**
     * reset dependency status
     */
    public abstract void reset();

    public void setMyjobid(long jobid) {
        this.myjobid = jobid;
    }

}
