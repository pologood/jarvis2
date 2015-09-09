/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:38:28
 */

package com.mogujie.jarvis.server.scheduler.dag.job;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
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
    private boolean hasTimeFlag = false;
    private boolean timeReadyFlag = false;

    public DAGJob() {
        this.parents = new LinkedList<DAGJob>();
        this.children = new LinkedList<DAGJob>();
    }

    public DAGJob(long jobId, AbstractDependStatus dependStatus, JobDependencyStrategy dependStrategy) {
        this.jobId = jobId;
        this.dependStatus = dependStatus;
        this.dependStrategy = dependStrategy;
        this.parents = new LinkedList<DAGJob>();
        this.children = new LinkedList<DAGJob>();
    }

    @Override
    public boolean dependCheck() {
        boolean passCheck = false;
        Set<Long> needJobs = new HashSet<Long>();
        for (DAGJob d : parents) {
            needJobs.add(d.getJobId());
        }
        passCheck = dependStatus.isFinishAllJob(dependStrategy, needJobs);

        if (hasTimeFlag) {
            passCheck = passCheck && timeReadyFlag;
        }

        return passCheck;
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

    public void removeParent(long jobId) {
        Iterator<DAGJob> it = parents.iterator();
        while (it.hasNext()) {
            DAGJob parent = it.next();
            if (parent.getJobId() == jobId) {
                it.remove();
            }
        }
    }

    public void removeChild(long jobId) {
        Iterator<DAGJob> it = children.iterator();
        while (it.hasNext()) {
            DAGJob child = it.next();
            if (child.getJobId() == jobId) {
                it.remove();
            }
        }
    }

    public void removeParents() {
        List<DAGJob> parents = getParents();
        Iterator<DAGJob> it = parents.iterator();
        while (it.hasNext()) {
            DAGJob parent = it.next();
            it.remove();
            parent.removeChild(getJobId());
        }
    }

    public void removeChildren() {
        List<DAGJob> children = getChildren();
        Iterator<DAGJob> it = children.iterator();
        while (it.hasNext()) {
            DAGJob child = it.next();
            it.remove();
            child.removeParent(getJobId());
            child.removeDependStatus(getJobId());
        }
    }

    public void addReadyDependency(long jobId, long taskId) {
        dependStatus.addReadyDependency(jobId, taskId);
    }

    public void removeDependStatus(long jobId) {
        dependStatus.removeDependency(jobId);
    }

    public void resetDependStatus() {
        dependStatus.reset();
        if (hasTimeFlag) {
            resetTimeReadyFlag();
        }
    }

    public boolean isHasTimeFlag() {
        return hasTimeFlag;
    }

    public void setHasTimeFlag(boolean hasTimeFlag) {
        this.hasTimeFlag = hasTimeFlag;
    }

    public void setTimeReadyFlag() {
        timeReadyFlag = true;
    }

    public void resetTimeReadyFlag() {
        timeReadyFlag = false;
    }
}
