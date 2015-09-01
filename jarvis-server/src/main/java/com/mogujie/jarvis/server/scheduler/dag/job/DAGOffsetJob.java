/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:40:31
 */

package com.mogujie.jarvis.server.scheduler.dag.job;

import com.mogujie.jarvis.server.scheduler.dag.JobDependencyStrategy;
import com.mogujie.jarvis.server.scheduler.dag.status.IJobDependStatus;


/**
 * @author guangming
 *
 */
public class DAGOffsetJob extends DAGJob {

    /**
     * @param jobid
     * @param jobstatus
     * @param dependStrategy
     */
    public DAGOffsetJob(int jobid, IJobDependStatus jobstatus, JobDependencyStrategy dependStrategy) {
        super(jobid, jobstatus, dependStrategy);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean dependCheck() {
        // TODO Auto-generated method stub
        return false;
    }
}
