/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:49:39
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mogujie.jarvis.server.scheduler.dag.JobDependencyStrategy;

/**
 * @author guangming
 *
 */
public abstract class AbstractDependStatus {

    private long myjobId;

    /**
     * init
     */
    public abstract void init();

    /**
     * update ready dependency job status to true
     */
    public void setDependStatus(long jobId, long taskId) {
        modifyDependStatus(jobId, taskId, true);
    }

    /**
     * update ready dependency job status to false
     */
    public void resetDependStatus(long jobId, long taskId) {
        modifyDependStatus(jobId, taskId, false);
    }

    /**
     * remove job dependency
     */
    public abstract void removeDependency(long jobId);

    /**
     * return true if finished all jobs
     */
    public boolean isFinishAllJob(JobDependencyStrategy strategy, Set<Long> needJobs) {
        boolean finishDependencies = true;
        for (long jobId : needJobs) {
            if (!isFinishOneJob(getJobStatusMap(), strategy, jobId)) {
                finishDependencies = false;
                break;
            }
        }
        return finishDependencies;
    }

    protected abstract void modifyDependStatus(long jobId, long taskId, boolean status);

    protected abstract Map<Long, Map<Long, Boolean>> getJobStatusMap();

    protected boolean isFinishOneJob(Map<Long, Map<Long, Boolean>> jobStatusMap,
            JobDependencyStrategy strategy, Long jobId) {
        boolean finishDependency = false;
        Map<Long, Boolean> taskStatusMap = jobStatusMap.get(jobId);
        if (taskStatusMap != null) {
            // 多个执行计划中任意一次成功即算成功
            if (strategy.equals(JobDependencyStrategy.ANYONE)) {
                for (Map.Entry<Long, Boolean> entry : taskStatusMap.entrySet()) {
                    if (entry.getValue() == true) {
                        finishDependency = true;
                        break;
                    }
                }
            } else if (strategy.equals(JobDependencyStrategy.LASTONE)) {
                // 多个执行计划中最后一次成功算成功
                Iterator<Entry<Long, Boolean>> it = taskStatusMap.entrySet().iterator();
                Map.Entry<Long, Boolean> entry = null;
                while (it.hasNext()) {
                    entry = it.next();
                }
                if (entry != null && entry.getValue() == true) {
                    finishDependency = true;
                }
            } else if (strategy.equals(JobDependencyStrategy.ALL)) {
                // 多个执行计划中所有都成功才算成功
                finishDependency = true;
                for (Map.Entry<Long, Boolean> entry : taskStatusMap.entrySet()) {
                    if (entry.getValue() == false) {
                        finishDependency = false;
                        break;
                    }
                }
            }
        }

        return finishDependency;
    }

    /**
     * reset dependency status
     */
    public abstract void reset();

    public long getMyJobId() {
        return myjobId;
    }

    public void setMyjobId(long jobId) {
        this.myjobId = jobId;
    }
}
