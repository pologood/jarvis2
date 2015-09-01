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
 * The implementation of JobDependStatus with mysql mapping
 *
 * @author guangming
 *
 */
public class MysqlJobDependStatus implements IJobDependStatus {

    @Override
    public void addReadyDependency(long jobid, long taskid) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeReadyDependency(long jobid, long taskid) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeDependency(long jobid) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isFinishAllJob(JobDependencyStrategy strategy, Set<Long> needJobs) {
        // TODO Auto-generated method stub
        return false;
    }

}
