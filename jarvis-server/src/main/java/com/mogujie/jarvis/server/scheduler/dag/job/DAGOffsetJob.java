/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:40:31
 */

package com.mogujie.jarvis.server.scheduler.dag.job;

import com.mogujie.jarvis.server.scheduler.dag.JobDependencyStrategy;
import com.mogujie.jarvis.server.scheduler.dag.status.AbstractDependStatus;


/**
 * @author guangming
 *
 */
public class DAGOffsetJob extends DAGJob {

    /**
     * @param jobId
     * @param jobstatus
     * @param dependStrategy
     */
    public DAGOffsetJob(long jobId, AbstractDependStatus jobstatus, JobDependencyStrategy dependStrategy) {
        super(jobId, jobstatus, dependStrategy);
    }

    @Override
    public boolean dependCheck() {
        // TODO Auto-generated method stub
        return false;
    }
}
