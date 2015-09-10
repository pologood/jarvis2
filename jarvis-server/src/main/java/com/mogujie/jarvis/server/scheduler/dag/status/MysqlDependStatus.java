/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午1:29:59
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import java.util.Set;

import com.mogujie.jarvis.server.scheduler.dag.JobDependencyStrategy;

/**
 * The implementation of AbstractDependStatus with mysql mapping
 *
 * @author guangming
 *
 */
public class MysqlDependStatus extends AbstractDependStatus {

    @Override
    public void setDependStatus(long jobId, long taskId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void resetDependStatus(long jobId, long taskId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeDependency(long jobId) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isFinishAllJob(JobDependencyStrategy strategy, Set<Long> needJobs) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

}
