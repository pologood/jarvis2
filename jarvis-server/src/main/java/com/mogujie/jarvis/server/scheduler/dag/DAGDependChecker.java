/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 上午10:50:41
 */

package com.mogujie.jarvis.server.scheduler.dag;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.mogujie.jarvis.server.scheduler.dag.status.AbstractDependStatus;
import com.mogujie.jarvis.server.scheduler.dag.status.DependStatusFactory;
import com.mogujie.jarvis.server.service.JobDependService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class DAGDependChecker {
    private long myJobId;

    protected Map<Long, AbstractDependStatus> jobStatusMap = new ConcurrentHashMap<Long, AbstractDependStatus>();

    private JobDependService jobDependService;

    public DAGDependChecker() {
        jobDependService = SpringContext.getBean(JobDependService.class);
    }

    public DAGDependChecker(long jobId) {
        this.myJobId = jobId;
    }

    public long getMyJobId() {
        return myJobId;
    }

    public void setMyJobId(long myJobId) {
        this.myJobId = myJobId;
    }

    public boolean check(Set<Long> needJobs) {
        boolean finishDependencies = true;
        for (long jobId : needJobs) {
            AbstractDependStatus taskDependStatus = jobStatusMap.get(jobId);
            if (taskDependStatus == null) {
                try {
                    taskDependStatus = DependStatusFactory.createDependStatus(jobDependService, myJobId, jobId);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            if (!taskDependStatus.check()) {
                finishDependencies = false;
                break;
            }
        }

        autoFix(needJobs);

        return finishDependencies;
    }

    public void removeDependency(long jobId) {
        AbstractDependStatus taskDependStatus = jobStatusMap.get(jobId);
        if (taskDependStatus != null) {
            taskDependStatus.removeDependency();
            jobStatusMap.remove(jobId);
        }
    }

    public void setDependStatus(long jobId, long taskId) {
        AbstractDependStatus taskDependStatus = jobStatusMap.get(jobId);
        if (taskDependStatus != null) {
            taskDependStatus.setDependStatus(taskId);
        }
    }

    public void resetDependStatus(long jobId, long taskId) {
        AbstractDependStatus taskDependStatus = jobStatusMap.get(jobId);
        if (taskDependStatus != null) {
            taskDependStatus.resetDependStatus(taskId);
        }
    }

    public void resetAllStatus() {
        for (AbstractDependStatus taskDependStatus : jobStatusMap.values()) {
            taskDependStatus.reset();
        }
    }

    private void autoFix(Set<Long> needJobs) {
        Set<Long> haveJobs = jobStatusMap.keySet();
        for (long jobId : haveJobs) {
            if (!needJobs.contains(jobId)) {
                AbstractDependStatus taskDependStatus = jobStatusMap.get(jobId);
                taskDependStatus.reset();
                jobStatusMap.remove(jobId);
            }
        }
    }

}
