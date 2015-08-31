/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:38:28
 */

package com.mogujie.jarvis.server.scheduler.dag.job;

import java.util.ArrayList;
import java.util.List;

import com.mogujie.jarvis.server.scheduler.dag.JobDependencyStrategy;
import com.mogujie.jarvis.server.scheduler.dag.status.IJobDependStatus;

/**
 * @author guangming
 *
 */
public class DAGJob implements IDAGJob {

    private int jobid;
    private IJobDependStatus jobstatus;
    private JobDependencyStrategy dependStrategy;
    private List<DAGJob> parents;
    private List<DAGJob> children;

    public DAGJob() {
        this.parents = new ArrayList<DAGJob>();
        this.children = new ArrayList<DAGJob>();
    }

    public DAGJob(int jobid, IJobDependStatus jobstatus, JobDependencyStrategy dependStrategy) {
        try {
            this.jobid = jobid;
            this.jobstatus = jobstatus;
            this.dependStrategy = dependStrategy;
            this.parents = new ArrayList<DAGJob>();
            this.children = new ArrayList<DAGJob>();
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

    public JobDependencyStrategy getDependStrategy() {
        return dependStrategy;
    }

    public void setDependStrategy(JobDependencyStrategy dependStrategy) {
        this.dependStrategy = dependStrategy;
    }

    public List<DAGJob> getParents() {
        return parents;
    }

    public void setParents(List<DAGJob> parents) {
        this.parents = parents;
    }

    public List<DAGJob> getChildren() {
        return children;
    }

    public void setChildren(List<DAGJob> children) {
        this.children = children;
    }

    public void addParent(DAGJob newParent) {
        boolean isContain = false;
        for (DAGJob parent : parents) {
            if (parent.getJobid() == newParent.getJobid()) {
                isContain = true;
                break;
            }
        }

        if (!isContain) {
            parents.add(newParent);
        }
    }

    public void addChild(DAGJob newChild) {
        boolean isContain = false;
        for (DAGJob child : children) {
            if (child.getJobid() == newChild.getJobid()) {
                isContain = true;
                break;
            }
        }

        if (!isContain) {
            children.add(newChild);
        }
    }

    public void removeParent(DAGJob oldParent) {
        for (DAGJob parent : parents) {
            if (parent.getJobid() == oldParent.getJobid()) {
                parents.remove(parent);
            }
        }
    }

    public void removeChild(DAGJob oldChild) {
        for (DAGJob child : children) {
            if (child.getJobid() == oldChild.getJobid()) {
                children.remove(children);
            }
        }
    }
}
