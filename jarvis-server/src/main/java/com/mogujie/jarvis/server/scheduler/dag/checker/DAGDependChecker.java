/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 上午10:50:41
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.mogujie.jarvis.server.scheduler.depend.strategy.CommonStrategy;

/**
 * @author guangming
 *
 */
public abstract class DAGDependChecker {
    private long myJobId;

    // Map<JobId, AbstractTaskSchedule>
    protected Map<Long, AbstractTaskSchedule> jobScheduleMap =
            new ConcurrentHashMap<Long, AbstractTaskSchedule>();

    public DAGDependChecker() {
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

    public void removeDependency(long jobId) {
        AbstractTaskSchedule taskSchedule = jobScheduleMap.get(jobId);
        if (taskSchedule != null) {
            taskSchedule.resetSchedule();
            jobScheduleMap.remove(jobId);
        }
    }

    public void scheduleTask(long jobId, long taskId, long scheduleTime) {
        AbstractTaskSchedule taskSchedule = jobScheduleMap.get(jobId);

        if (taskSchedule == null) {
            taskSchedule = getSchedule(myJobId, jobId);
            if (taskSchedule != null) {
                jobScheduleMap.put(jobId, taskSchedule);
            }
        }

        if (taskSchedule != null) {
            taskSchedule.scheduleTask(taskId, scheduleTime);
        }
    }

    public boolean checkDependency(Set<Long> needJobs) {
        boolean finishDependencies = true;
        for (long jobId : needJobs) {
            AbstractTaskSchedule taskSchedule = jobScheduleMap.get(jobId);
            if (taskSchedule == null) {
                taskSchedule = getSchedule(myJobId, jobId);
                if (taskSchedule != null) {
                    jobScheduleMap.put(jobId, taskSchedule);
                }
            }
            if (taskSchedule == null || !taskSchedule.check()) {
                finishDependencies = false;
                break;
            }
        }

        autoFix(needJobs);

        return finishDependencies;
    }

    /**
     * return Map<JobId, Set<preTaskId>>
     *
     */
    public Map<Long, Set<Long>> getDependTaskIdMap() {
        Map<Long, Set<Long>> dependTaskMap = new HashMap<Long, Set<Long>>();
        for (Entry<Long, AbstractTaskSchedule> entry : jobScheduleMap.entrySet()) {
            dependTaskMap.put(entry.getKey(), entry.getValue().getSchedulingTaskIds());
        }
        return dependTaskMap;
    }

    public void resetAllSchedule() {
        for (AbstractTaskSchedule taskSchedule : jobScheduleMap.values()) {
            taskSchedule.resetSchedule();
        }
    }

    public void updateCommonStrategy(long parentId, CommonStrategy newStrategy) {
        AbstractTaskSchedule status = jobScheduleMap.get(parentId);
        if (status != null) {
            status.setCommonStrategy(newStrategy);
        }
    }

    private void autoFix(Set<Long> needJobs) {
        Iterator<Entry<Long, AbstractTaskSchedule>> it = jobScheduleMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Long, AbstractTaskSchedule> entry = it.next();
            long jobId = entry.getKey();
            if (!needJobs.contains(jobId)) {
                entry.getValue().resetSchedule();
                it.remove();
            }
        }
    }

    protected abstract AbstractTaskSchedule getSchedule(long myJobId, long preJobId);
}
