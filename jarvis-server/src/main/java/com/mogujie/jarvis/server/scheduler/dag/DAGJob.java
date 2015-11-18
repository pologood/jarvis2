/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:38:28
 */

package com.mogujie.jarvis.server.scheduler.dag;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.server.scheduler.dag.checker.DAGDependChecker;
import com.mogujie.jarvis.server.scheduler.dag.checker.ScheduleTask;

/**
 * @author guangming
 *
 */
public class DAGJob extends AbstractDAGJob {

    private long jobId;
    private DAGDependChecker dependChecker;
    private DAGJobType type;
    private boolean timeReadyFlag = false;
    private Set<Long> parents;
    private static final Logger LOGGER = LogManager.getLogger();

    public DAGJob(long jobId, DAGJobType type) {
        this.jobId = jobId;
        this.type = type;
        this.parents = Sets.newConcurrentHashSet();
        this.dependChecker = new DAGDependChecker(jobId);
    }

    @Override
    public boolean checkDependency(Set<Long> needJobs) {
        boolean passCheck = true;
        if (type.implies(DAGJobType.DEPEND)) {
            boolean dependCheck = dependChecker.checkDependency(needJobs);
            if (!dependCheck) {
                LOGGER.debug("dependChecker failed, job {}, needJobs {}", jobId, needJobs);
            }
            passCheck = passCheck && dependCheck;
        }

        if (type.implies(DAGJobType.TIME)) {
            if (!timeReadyFlag) {
                LOGGER.debug("Job {} is not time ready", jobId);
            }
            passCheck = passCheck && timeReadyFlag;
        }

        return passCheck;
    }

    public void scheduleTask(long jobId, long taskId, long scheduleTime) {
        dependChecker.scheduleTask(jobId, taskId, scheduleTime);
    }

    public Map<Long, List<ScheduleTask>> getDependTaskMap() {
        return dependChecker.getDependTaskIdMap();
    }

    @Override
    public String toString() {
        return "{[jobId is " + jobId + "]," +
                "[DAG type is " + type + "]," +
                "[depend check instance is " + dependChecker.getClass().getSimpleName() + "]}";
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
        this.dependChecker.setMyJobId(jobId);
    }

    public void resetTaskSchedule() {
        dependChecker.finishAllSchedule();
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

    public void addParent(long jobId) {
        parents.add(jobId);
    }

    public void removeParent(long jobId) {
        parents.remove(jobId);
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
