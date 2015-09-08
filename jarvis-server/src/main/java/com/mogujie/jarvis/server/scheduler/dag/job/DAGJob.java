/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:38:28
 */

package com.mogujie.jarvis.server.scheduler.dag.job;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mogujie.jarvis.server.scheduler.dag.JobDependencyStrategy;
import com.mogujie.jarvis.server.scheduler.dag.status.AbstractDependStatus;

/**
 * @author guangming
 *
 */
public class DAGJob implements IDAGJob {

    private long jobId;
    private AbstractDependStatus dependStatus;
    private JobDependencyStrategy dependStrategy;
    private List<DAGJob> parents;
    private List<DAGJob> children;

    public DAGJob() {
        this.parents = new ArrayList<DAGJob>();
        this.children = new ArrayList<DAGJob>();
    }

    public DAGJob(long jobId, AbstractDependStatus dependStatus, JobDependencyStrategy dependStrategy) {
        try {
            this.jobId = jobId;
            this.dependStatus = dependStatus;
            this.dependStrategy = dependStrategy;
            this.parents = new ArrayList<DAGJob>();
            this.children = new ArrayList<DAGJob>();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean dependCheck() {
        Set<Long> needJobs = new HashSet<Long>();
        for (DAGJob d : parents) {
            needJobs.add(d.getJobId());
        }
        return dependStatus.isFinishAllJob(dependStrategy, needJobs);
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public AbstractDependStatus getDependStatus() {
        return dependStatus;
    }

    public void setDependStatus(AbstractDependStatus dependStatus) {
        this.dependStatus = dependStatus;
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
            if (parent.getJobId() == newParent.getJobId()) {
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
            if (child.getJobId() == newChild.getJobId()) {
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
            if (parent.getJobId() == oldParent.getJobId()) {
                parents.remove(parent);
            }
        }
    }

    public void removeChild(DAGJob oldChild) {
        for (DAGJob child : children) {
            if (child.getJobId() == oldChild.getJobId()) {
                children.remove(children);
            }
        }
    }

    public void addReadyDependency(long jobId, long taskId) {
        dependStatus.addReadyDependency(jobId, taskId);
    }

    public void removeDenpendency(long jobId) {
        dependStatus.removeDependency(jobId);
    }

    public void resetDependStatus() {
        dependStatus.reset();
    }
}
