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
public abstract class CachedJobDependStatus extends IJobDependStatus {
    // Map<jobid, Map<taskid, status>>
    protected Map<Long, Map<Long, Boolean>> jobStatusMap =
            new ConcurrentHashMap<Long, Map<Long, Boolean>>();

    @Override
    public void addReadyDependency(long jobid, long taskid) {
        boolean status = true;
        if (!jobStatusMap.containsKey(jobid)) {
            Map<Long, Boolean> taskStatusMap = new ConcurrentHashMap<Long, Boolean>();
            taskStatusMap.put(taskid, status);
            jobStatusMap.put(jobid, taskStatusMap);
        } else {
            Map<Long, Boolean> planStatusMap = jobStatusMap.get(jobid);
            planStatusMap.put(taskid, status);
        }
    }

    @Override
    public void removeReadyDependency(long jobid, long taskid) {
        boolean status = false;
        if (jobStatusMap.containsKey(jobid)) {
            Map<Long, Boolean> taskStatusMap = jobStatusMap.get(jobid);
            if (taskStatusMap != null && taskStatusMap.containsKey(taskid)) {
                taskStatusMap.put(taskid, status);
            }
        }
    }

    @Override
    public void removeDependency(long jobid) {
        if (jobStatusMap.containsKey(jobid)) {
            Map<Long, Boolean> taskStatusMap = jobStatusMap.get(jobid);
            if (taskStatusMap != null) {
                taskStatusMap.clear();
            }
            jobStatusMap.remove(jobid);
        }
    }

    @Override
    public void reset() {
        jobStatusMap.clear();
    }

    public boolean isFinishAllJob(JobDependencyStrategy strategy, Set<Long> needJobs) {
        boolean finishDependencies = true;
        for (long jobid : needJobs) {
            if (!isFinishOneJob(strategy, jobid)) {
                finishDependencies = false;
                break;
            }
        }
        return finishDependencies;
    }

    private boolean isFinishOneJob(JobDependencyStrategy strategy, Long jobid) {
        boolean finishDependency = false;
        Map<Long, Boolean> taskStatusMap = jobStatusMap.get(jobid);
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
