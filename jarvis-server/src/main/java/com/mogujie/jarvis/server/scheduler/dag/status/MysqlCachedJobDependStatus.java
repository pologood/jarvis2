/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午2:13:48
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

/**
 * @author guangming
 *
 */
public class MysqlCachedJobDependStatus extends CachedJobDependStatus {

    @Override
    public void addReadyDependency(int jobid, int taskid) {
        super.addReadyDependency(jobid, taskid);
        flush2DB();
    }

    @Override
    public void removeReadyDependency(int jobid, int taskid) {
        super.removeReadyDependency(jobid, taskid);
        flush2DB();
    }

    @Override
    public void removeDependency(int jobid) {
        super.removeDependency(jobid);
        flush2DB();
    }

    @Override
    public void clear() {
        super.clear();
        flush2DB();
    }

    public void flush2DB() {
        //TODO flush to mysql
    }
}
