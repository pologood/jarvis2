/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:38:56
 */

package com.mogujie.jarvis.server.scheduler.dag.job;

import com.mogujie.jarvis.server.scheduler.dag.JobDependencyStrategy;
import com.mogujie.jarvis.server.scheduler.dag.status.AbstractDependStatus;


/**
 * @author guangming
 *
 */
public class TimeDAGJob extends DAGJob {

    /**
     * @param jobId
     * @param jobstatus
     * @param dependStrategy
     */
    public TimeDAGJob(long jobId, AbstractDependStatus jobstatus, JobDependencyStrategy dependStrategy) {
        super(jobId, jobstatus, dependStrategy);
        // TODO Auto-generated constructor stub
    }

    private boolean timeReadyFlag = false;

    @Override
    public boolean dependCheck() {
        if (super.dependCheck() && timeReadyFlag) {
            return true;
        }

        return false;
    }

    public void setTimeFlag() {
        timeReadyFlag = true;
    }

    public void resetTimeFlag() {
        timeReadyFlag = false;
    }

    @Override
    public void resetDependStatus() {
        super.resetDependStatus();
        resetTimeFlag();
    }
}
