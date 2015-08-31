/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午1:29:59
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

/**
 * The implementation of JobDependStatus with mysql mapping
 *
 * @author guangming
 *
 */
public class MysqlJobDependStatus implements IJobDependStatus {

    @Override
    public void addReadyDependency(int jobid, int taskid) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeReadyDependency(int jobid, int taskid) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeDependency(int jobid) {
        // TODO Auto-generated method stub

    }

}
