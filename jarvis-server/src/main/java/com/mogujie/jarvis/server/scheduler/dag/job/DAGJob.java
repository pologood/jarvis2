/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:38:28
 */

package com.mogujie.jarvis.server.scheduler.dag.job;

import java.util.Set;

import com.mogujie.jarvis.server.scheduler.dag.checker.DAGDependChecker;
import com.mogujie.jarvis.server.scheduler.dag.checker.DAGDependCheckerFactory;

/**
 * @author guangming
 *
 */
public class DAGJob extends AbstractDAGJob {

    private long jobId;
    private DAGDependChecker dependChecker;
    private boolean hasTimeFlag = false;
    private boolean timeReadyFlag = false;

    public DAGJob() {
        this.dependChecker = DAGDependCheckerFactory.create();
    }

    public DAGJob(long jobId) {
        this.jobId = jobId;
        this.dependChecker = DAGDependCheckerFactory.create();
    }

    @Override
    public boolean dependCheck(Set<Long> needJobs) {
        boolean passCheck = false;
        passCheck = dependChecker.check(needJobs);

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
        this.dependChecker.setMyJobId(jobId);
    }

    public void setDependStatus(long jobId, long taskId) {
        dependChecker.setDependStatus(jobId, taskId);
    }

    public void resetDependStatus(long jobId, long taskId) {
        dependChecker.resetDependStatus(jobId, taskId);
    }

    public void resetDependStatus() {
        dependChecker.resetAllStatus();
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

    public DAGDependChecker getDependChecker() {
        return dependChecker;
    }

    public void setDependChecker(DAGDependChecker dependChecker) {
        this.dependChecker = dependChecker;
    }
}
