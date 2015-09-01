/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:49:39
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

/**
 * @author guangming
 *
 */
public interface IJobDependStatus {

    /**
     * update ready dependency job status to true
     */
    public void addReadyDependency(int jobid, int taskid);

    /**
     * update ready dependency job status to false
     */
    public void removeReadyDependency(int jobid, int taskid);

    /**
     * remove job dependency
     */
    public void removeDependency(int jobid);

}
