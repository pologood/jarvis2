/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:38:28
 */

package com.mogujie.jarvis.server.scheduler.DAG.job;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.DAG.status.IJobDependStatus;
import com.mogujie.jarvis.server.scheduler.DAG.status.JobDependencyStrategy;

/**
 * @author guangming
 *
 */
public class DAGJob implements IDAGJob {

    private int jobid;
    private IJobDependStatus jobstatus;
    private JobDependencyStrategy dependStrategy;
    private List<Integer> parents;
    private List<Integer> children;

    public DAGJob(Configuration conf, int jobid, JobDependencyStrategy dependStrategy, List<Integer> parents, List<Integer> children) {
        try {
            this.jobid = jobid;
            this.jobstatus = SchedulerUtil.getJobDependStatus(conf);
            this.dependStrategy = dependStrategy;
            this.parents = parents;
            this.children = children;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean dependCheck() {
        // TODO Auto-generated method stub
        return false;
    }

    public int getJobid() {
        return jobid;
    }

    public void setJobid(int jobid) {
        this.jobid = jobid;
    }

    public IJobDependStatus getJobstatus() {
        return jobstatus;
    }

    public void setJobstatus(IJobDependStatus jobstatus) {
        this.jobstatus = jobstatus;
    }

    public List<Integer> getParents() {
        return parents;
    }

    public void setParents(List<Integer> parents) {
        this.parents = parents;
    }

    public List<Integer> getChildren() {
        return children;
    }

    public void setChildren(List<Integer> children) {
        this.children = children;
    }
}
