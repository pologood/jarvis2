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
 * @author guangming
 *
 */
public abstract class CachedJobDependStatus implements IJobDependStatus {
    // Map<jobid, Map<taskid, status>>
    protected Map<Integer, Map<Integer, Boolean>> jobStatusMap =
            new ConcurrentHashMap<Integer, Map<Integer, Boolean>>();

    @Override
    public void addReadyDependency(int jobid, int taskid) {
        boolean status = true;
        if (!jobStatusMap.containsKey(jobid)) {
            Map<Integer, Boolean> taskStatusMap = new ConcurrentHashMap<Integer, Boolean>();
            taskStatusMap.put(taskid, status);
            jobStatusMap.put(jobid, taskStatusMap);
        } else {
            Map<Integer, Boolean> planStatusMap = jobStatusMap.get(jobid);
            planStatusMap.put(taskid, status);
        }
    }

    @Override
    public void removeReadyDependency(int jobid, int taskid) {
        boolean status = false;
        if (jobStatusMap.containsKey(jobid)) {
            Map<Integer, Boolean> taskStatusMap = jobStatusMap.get(jobid);
            if (taskStatusMap != null && taskStatusMap.containsKey(taskid)) {
                taskStatusMap.put(taskid, status);
            }
        }
    }

    @Override
    public void removeDependency(int jobid) {
        if (jobStatusMap.containsKey(jobid)) {
            Map<Integer, Boolean> taskStatusMap = jobStatusMap.get(jobid);
            if (taskStatusMap != null) {
                taskStatusMap.clear();
            }
            jobStatusMap.remove(jobid);
        }
    }

    /**
     * clear cache of jobStatusMap
     */
    public void clear() {
        jobStatusMap.clear();
    }

    /**
     * return true if finished all jobs
     */
    public boolean isFinishAllJob(JobDependencyStrategy strategy, Set<Integer> needJobs) {
        boolean finishDependencies = true;
        for (int jobid : needJobs) {
            if (!isFinishOneJob(strategy, jobid)) {
                finishDependencies = false;
                break;
            }
        }
        return finishDependencies;
    }

    private boolean isFinishOneJob(JobDependencyStrategy strategy, int jobid) {
        boolean finishDependency = false;
        Map<Integer, Boolean> taskStatusMap = jobStatusMap.get(jobid);
        // 多个执行计划中任意一次成功即算成功
        if (strategy.equals(JobDependencyStrategy.ANYONE)) {
            for (Map.Entry<Integer, Boolean> entry : taskStatusMap.entrySet()) {
                if (entry.getValue() == true) {
                    finishDependency = true;
                    break;
                }
            }
        } else if (strategy.equals(JobDependencyStrategy.LASTONE)) {
            // 多个执行计划中最后一次成功算成功
            Iterator<Entry<Integer, Boolean>> it = taskStatusMap.entrySet().iterator();
            Map.Entry<Integer, Boolean> entry = null;
            while (it.hasNext()) {
                entry = it.next();
            }
            if (entry != null && entry.getValue() == true) {
                finishDependency = true;
            }
        } else if (strategy.equals(JobDependencyStrategy.ALL)) {
            // 多个执行计划中所有都成功才算成功
            for (Map.Entry<Integer, Boolean> entry : taskStatusMap.entrySet()) {
                if (entry.getValue() == false) {
                    finishDependency = false;
                    break;
                }
            }
        }

        return finishDependency;
    }
}
