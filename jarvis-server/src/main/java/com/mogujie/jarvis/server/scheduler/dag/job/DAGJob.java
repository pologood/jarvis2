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

import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.server.scheduler.dag.JobDependencyStrategy;
import com.mogujie.jarvis.server.scheduler.dag.status.AbstractDependStatus;

/**
 * @author guangming
 *
 */
public class DAGJob extends AbstractDAGJob {

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
        dependStatus.init();
    }

    @Override
    public boolean dependCheck() {
        boolean passCheck = false;
        Set<Long> needJobs = new HashSet<Long>();
        for (DAGJob d : parents) {
            if (d.getJobFlag().equals(JobFlag.ENABLE)) {
                needJobs.add(d.getJobId());
            }
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

    /**
     * Just add parent to this
     *
     */
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

    /**
     * Just add child to this
     *
     */
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

    /**
     * Just remove parent from this
     *
     */
    public void removeParent(DAGJob oldParent) {
        long jobId = oldParent.getJobId();
        Iterator<DAGJob> it = parents.iterator();
        while (it.hasNext()) {
            DAGJob parent = it.next();
            if (parent.getJobId() == jobId) {
                it.remove();
            }
        }
        dependStatus.removeDependency(jobId);
    }

    /**
     * Just remove child from this
     *
     */
    public void removeChild(DAGJob oldChild) {
        long jobId = oldChild.getJobId();
        Iterator<DAGJob> it = children.iterator();
        while (it.hasNext()) {
            DAGJob child = it.next();
            if (child.getJobId() == jobId) {
                it.remove();
            }
        }
    }

    public void removeParents() {
        removeParents(true);
    }

    /**
     * This method will also remove this from parents
     * If removeDependStatus is true, will also remove depend status
     *
     */
    public void removeParents(boolean removeDependStatus) {
        List<DAGJob> parents = getParents();
        Iterator<DAGJob> it = parents.iterator();
        while (it.hasNext()) {
            // 1. remove parent from myself
            DAGJob parent = it.next();
            it.remove();
            if (removeDependStatus) {
                dependStatus.removeDependency(parent.getJobId());
            }
            // 2. remove myself from parent
            parent.removeChild(this);
        }
    }

    public void removeChildren() {
        removeChildren(true);
    }

    /**
     * This method will also remove this from children
     *
     */
    public void removeChildren(boolean removeDependStatus) {
        List<DAGJob> children = getChildren();
        Iterator<DAGJob> it = children.iterator();
        while (it.hasNext()) {
            // 1. remove child from myself
            DAGJob child = it.next();
            it.remove();
            // 2. remove myself from child
            child.removeParent(this);
            if (removeDependStatus) {
                child.getDependStatus().removeDependency(getJobId());
            }
        }
    }

    public void setDependStatus(long jobId, long taskId) {
        dependStatus.setDependStatus(jobId, taskId);
    }

    public void resetDependStatus(long jobId, long taskId) {
        dependStatus.resetDependStatus(jobId, taskId);
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
