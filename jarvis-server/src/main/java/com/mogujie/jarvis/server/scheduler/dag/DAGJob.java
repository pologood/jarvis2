/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:38:28
 */

package com.mogujie.jarvis.server.scheduler.dag;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.server.domain.JobKey;
import com.mogujie.jarvis.server.scheduler.dag.checker.DAGDependChecker;
import com.mogujie.jarvis.server.scheduler.dag.checker.DAGDependCheckerFactory;

/**
 * @author guangming
 *
 */
public class DAGJob extends AbstractDAGJob {

    private JobKey jobKey;
    private DAGDependChecker dependChecker;
    private DAGJobType type;
    private boolean timeReadyFlag = false;
    private static final Logger LOGGER = LogManager.getLogger();

    public DAGJob() {
        this.dependChecker = DAGDependCheckerFactory.create();
    }

    public DAGJob(JobKey jobKey, DAGJobType type) {
        this();
        this.jobKey = jobKey;
        this.type = type;
    }

    public DAGJob(JobKey jobKey, DAGJobType type, JobFlag jobFlag) {
        this(jobKey, type);
        setJobFlag(jobFlag);
    }

    @Override
    public boolean dependCheck(Set<JobKey> needJobs) {
        boolean passCheck = true;
        if (type.implies(DAGJobType.DEPEND)) {
            boolean dependCheck = dependChecker.check(needJobs);
            if (!dependCheck) {
                LOGGER.debug("dependChecker failed, job {}, needJobs {}", jobKey, needJobs);
            }
            passCheck = passCheck && dependChecker.check(needJobs);
        }

        if (type.implies(DAGJobType.TIME)) {
            if (!timeReadyFlag) {
                LOGGER.debug("Job {} is not time ready", jobKey);
            }
            passCheck = passCheck && timeReadyFlag;
        }

        return passCheck;
    }

    @Override
    public String toString() {
        return "{[jobKey is" + jobKey + "]," +
                "[DAG type is" + type + "]," +
                "[depend check instance is" + dependChecker.getClass().getSimpleName() + "]}";
    }

    public JobKey getJobKey() {
        return jobKey;
    }

    public void setJobKey(JobKey jobKey) {
        this.jobKey = jobKey;
        this.dependChecker.setMyJobKey(jobKey);
    }

    public void setDependStatus(JobKey jobKey, long taskId) {
        dependChecker.setDependStatus(jobKey, taskId);
    }

    public void resetDependStatus(JobKey jobKey, long taskId) {
        dependChecker.resetDependStatus(jobKey, taskId);
    }

    public void resetDependStatus() {
        dependChecker.resetAllStatus();
        if (type.implies(DAGJobType.TIME)) {
            resetTimeReadyFlag();
        }
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

    public DAGJobType getType() {
        return type;
    }

    public void setType(DAGJobType type) {
        this.type = type;
    }

    public void updateJobTypeByTimeFlag(boolean timeFlag) {
        updateJobType(timeFlag, DAGJobType.TIME);
    }

    public void updateJobTypeByDependFlag(boolean dependFlag) {
        updateJobType(dependFlag, DAGJobType.DEPEND);
    }

    public void updateJobTypeByCycleFlag(boolean cycleFlag) {
        updateJobType(cycleFlag, DAGJobType.CYCLE);
    }

    private void updateJobType(boolean isAdd, DAGJobType that) {
        if (isAdd) {
            type = type.or(that);
        } else {
            type = type.remove(that);
        }
    }
}
