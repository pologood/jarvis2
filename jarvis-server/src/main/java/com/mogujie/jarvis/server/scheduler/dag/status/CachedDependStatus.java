/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午1:52:49
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.mogujie.jarvis.server.scheduler.dag.JobDependencyStrategy;

/**
 * The implementation of AbstractDependStatus with cached map
 *
 * @author guangming
 *
 */
public class CachedDependStatus extends AbstractDependStatus {
    // Map<jobId, Map<taskId, status>>
    protected Map<Long, Map<Long, Boolean>> jobStatusMap =
            new ConcurrentHashMap<Long, Map<Long, Boolean>>();

    @Override
    public void addReadyDependency(long jobId, long taskId) {
        boolean status = true;
        if (!jobStatusMap.containsKey(jobId)) {
            Map<Long, Boolean> taskStatusMap = new ConcurrentHashMap<Long, Boolean>();
            taskStatusMap.put(taskId, status);
            jobStatusMap.put(jobId, taskStatusMap);
        } else {
            Map<Long, Boolean> planStatusMap = jobStatusMap.get(jobId);
            planStatusMap.put(taskId, status);
        }
    }

    @Override
    public void removeReadyDependency(long jobId, long taskId) {
        boolean status = false;
        if (jobStatusMap.containsKey(jobId)) {
            Map<Long, Boolean> taskStatusMap = jobStatusMap.get(jobId);
            if (taskStatusMap != null && taskStatusMap.containsKey(taskId)) {
                taskStatusMap.put(taskId, status);
            }
        }
    }

    @Override
    public void removeDependency(long jobId) {
        if (jobStatusMap.containsKey(jobId)) {
            Map<Long, Boolean> taskStatusMap = jobStatusMap.get(jobId);
            if (taskStatusMap != null) {
                taskStatusMap.clear();
            }
            jobStatusMap.remove(jobId);
        }
    }

    @Override
    public void reset() {
        jobStatusMap.clear();
    }

    public boolean isFinishAllJob(JobDependencyStrategy strategy, Set<Long> needJobs) {
        boolean finishDependencies = true;
        for (long jobId : needJobs) {
            if (!isFinishOneJob(strategy, jobId)) {
                finishDependencies = false;
                break;
            }
        }
        return finishDependencies;
    }

    private boolean isFinishOneJob(JobDependencyStrategy strategy, Long jobId) {
        boolean finishDependency = false;
        Map<Long, Boolean> taskStatusMap = jobStatusMap.get(jobId);
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
            for (Map.Entry<Long, Boolean> entry : taskStatusMap.entrySet()) {
                if (entry.getValue() == false) {
                    finishDependency = false;
                    break;
                }
            }
        }

        return finishDependency;
    }
}
