/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:40:31
 */

package com.mogujie.jarvis.server.scheduler.DAG.job;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.server.scheduler.DAG.status.JobDependencyStrategy;

/**
 * @author guangming
 *
 */
public class DAGOffsetJob extends DAGJob {

    /**
     * @param conf
     * @param jobid
     * @param dependStrategy
     * @param parents
     * @param children
     */
    public DAGOffsetJob(Configuration conf, int jobid, JobDependencyStrategy dependStrategy,
            List<Integer> parents, List<Integer> children) {
        super(conf, jobid, dependStrategy, parents, children);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean dependCheck() {
        // TODO Auto-generated method stub
        return false;
    }
}
