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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guangming
 *
 */
public class DAGDependChecker {
    private long myJobId;

    // Map<JobId, TaskDependSchedule>
    protected Map<Long, TaskDependSchedule> jobScheduleMap =
            new ConcurrentHashMap<Long, TaskDependSchedule>();

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
        TaskDependSchedule taskSchedule = jobScheduleMap.get(jobId);
        if (taskSchedule != null) {
            jobScheduleMap.remove(jobId);
        }
    }

    public void addDependency(long jobId) {

    }

    public void scheduleTask(long jobId, long taskId, long scheduleTime) {
        TaskDependSchedule taskSchedule = jobScheduleMap.get(jobId);

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
            TaskDependSchedule taskSchedule = jobScheduleMap.get(jobId);
            if (taskSchedule == null) {
                taskSchedule = getSchedule(myJobId, jobId);
                if (taskSchedule != null) {
                    jobScheduleMap.put(jobId, taskSchedule);
                } else {
                    return false;
                }
            }
        }

        List<ScheduleTask> schedulingTasks = null;
        for (TaskDependSchedule taskSchedule : jobScheduleMap.values()) {
            // find one which not offset dependency
            // and size of scheduling tasks > 0
            if (taskSchedule.getSchedulingTasks().size() > 0) {
                schedulingTasks = taskSchedule.getSchedulingTasks();
                break;
            }
        }
        if (schedulingTasks != null) {
            for (ScheduleTask task : schedulingTasks) {
                long scheduleTime = task.getScheduleTime();
                for (TaskDependSchedule taskSchedule : jobScheduleMap.values()) {
                    if (!taskSchedule.check(scheduleTime)) {
                        finishDependencies = false;
                        resetAllSelected();
                        break;
                    }
                }
            }
        } else {
            finishDependencies = false;
        }

        autoFix(needJobs);

        return finishDependencies;
    }

    /**
     * return Map<JobId, Set<preTaskId>>
     *
     */
    public Map<Long, List<ScheduleTask>> getDependTaskIdMap() {
        Map<Long, List<ScheduleTask>> dependTaskMap = new HashMap<Long, List<ScheduleTask>>();
        for (Entry<Long, TaskDependSchedule> entry : jobScheduleMap.entrySet()) {
            dependTaskMap.put(entry.getKey(), entry.getValue().getSelectedTasks());
        }
        return dependTaskMap;
    }

    public void resetAllSelected() {
        for (TaskDependSchedule taskSchedule : jobScheduleMap.values()) {
            taskSchedule.resetSelected();
        }
    }

    public void finishAllSchedule() {
        for (TaskDependSchedule taskSchedule : jobScheduleMap.values()) {
            taskSchedule.finishSchedule();
        }
    }

    public void updateExpression(long parentId, String expression) {
        TaskDependSchedule dependSchedule = jobScheduleMap.get(parentId);
        if (dependSchedule != null) {
            dependSchedule.setExpression(expression);
        }
    }

    private void autoFix(Set<Long> needJobs) {
        Iterator<Entry<Long, TaskDependSchedule>> it = jobScheduleMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Long, TaskDependSchedule> entry = it.next();
            long jobId = entry.getKey();
            if (!needJobs.contains(jobId)) {
                entry.getValue().resetSelected();
                it.remove();
            }
        }
    }

    private TaskDependSchedule getSchedule(long myJobId, long preJobId) {
        return TaskScheduleFactory.create(myJobId, preJobId);
    }
}
