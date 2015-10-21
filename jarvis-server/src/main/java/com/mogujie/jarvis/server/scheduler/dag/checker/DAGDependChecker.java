/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 上午10:50:41
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.mogujie.jarvis.server.domain.JobKey;
import com.mogujie.jarvis.server.scheduler.dag.status.AbstractDependStatus;
import com.mogujie.jarvis.server.scheduler.dag.status.OffsetDependStatus;
import com.mogujie.jarvis.server.scheduler.dag.strategy.AbstractOffsetStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.CommonStrategy;

/**
 * @author guangming
 *
 */
public abstract class DAGDependChecker {
    private JobKey myJobKey;

    protected Map<JobKey, AbstractDependStatus> jobStatusMap = new ConcurrentHashMap<JobKey, AbstractDependStatus>();

    public DAGDependChecker() {
    }

    public DAGDependChecker(JobKey jobKey) {
        this.myJobKey = jobKey;
    }

    public JobKey getMyJobKey() {
        return myJobKey;
    }

    public void setMyJobKey(JobKey jobKey) {
        this.myJobKey = jobKey;
    }

    public boolean check(Set<JobKey> needJobs) {
        boolean finishDependencies = true;
        for (JobKey key : needJobs) {
            AbstractDependStatus taskDependStatus = jobStatusMap.get(key);
            if (taskDependStatus == null) {
                taskDependStatus = getDependStatus(myJobKey, key);
                if (taskDependStatus != null) {
                    jobStatusMap.put(key, taskDependStatus);
                }
            }
            if (taskDependStatus == null || !taskDependStatus.check()) {
                finishDependencies = false;
                break;
            }
        }

        autoFix(needJobs);

        return finishDependencies;
    }

    public void removeDependency(JobKey jobKey) {
        AbstractDependStatus taskDependStatus = jobStatusMap.get(jobKey);
        if (taskDependStatus != null) {
            taskDependStatus.reset();
            jobStatusMap.remove(jobKey);
        }
    }

    public void setDependStatus(JobKey jobKey, long taskId) {
        AbstractDependStatus taskDependStatus = jobStatusMap.get(jobKey);
        if (taskDependStatus == null) {
            taskDependStatus = getDependStatus(myJobKey, jobKey);
            if (taskDependStatus != null) {
                jobStatusMap.put(jobKey, taskDependStatus);
            }
        }

        if (taskDependStatus != null) {
            taskDependStatus.setDependStatus(taskId);
        }
    }

    public void resetDependStatus(JobKey jobKey, long taskId) {
        AbstractDependStatus taskDependStatus = jobStatusMap.get(jobKey);
        if (taskDependStatus == null) {
            taskDependStatus = getDependStatus(myJobKey, jobKey);
            if (taskDependStatus != null) {
                jobStatusMap.put(jobKey, taskDependStatus);
            }
        }

        if (taskDependStatus != null) {
            taskDependStatus.resetDependStatus(taskId);
        }
    }

    public void resetAllStatus() {
        for (AbstractDependStatus taskDependStatus : jobStatusMap.values()) {
            taskDependStatus.reset();
        }
    }

    public void updateCommonStrategy(JobKey parentKey, CommonStrategy newStrategy) {
        AbstractDependStatus status = jobStatusMap.get(parentKey);
        if (status != null) {
            status.setCommonStrategy(newStrategy);
        }
    }

    public void updateOffsetStrategy(JobKey parentKey, AbstractOffsetStrategy newStrategy) {
        AbstractDependStatus status = jobStatusMap.get(parentKey);
        if (status != null && status instanceof OffsetDependStatus) {
            ((OffsetDependStatus) status).setOffsetDependStrategy(newStrategy);
        }
    }

    private void autoFix(Set<JobKey> needJobs) {
        Iterator<Entry<JobKey, AbstractDependStatus>> it = jobStatusMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<JobKey, AbstractDependStatus> entry = it.next();
            JobKey jobKey = entry.getKey();
            if (!needJobs.contains(jobKey)) {
                entry.getValue().reset();
                it.remove();
            }
        }
    }

    protected abstract AbstractDependStatus getDependStatus(JobKey myJobKey, JobKey preJobKey);
}
