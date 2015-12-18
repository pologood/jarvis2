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

import org.apache.http.annotation.NotThreadSafe;
import org.joda.time.DateTime;

import com.google.common.collect.Maps;
import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.core.expression.TimeOffsetExpression;
import com.mogujie.jarvis.server.domain.JobDependencyEntry;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.service.JobService;

/**
 * 单个任务的依赖检查器，内部维护Map<Long, TaskDependSchedule> jobScheduleMap进行依赖检查。
 * jobScheduleMap不是线程安全的数据结构，但是当前外部都是同步调用，不会有问题。
 *
 * @author guangming
 *
 */
@NotThreadSafe
public class DAGDependChecker {
    private long myJobId;

    // Map<JobId, TaskDependSchedule>
    protected Map<Long, TaskDependSchedule> jobScheduleMap = Maps.newHashMap();

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

    public void scheduleTask(long jobId, long taskId, long scheduleTime) {
        TaskDependSchedule taskSchedule = jobScheduleMap.get(jobId);

        if (taskSchedule == null) {
            taskSchedule = getSchedule(myJobId, jobId);
            jobScheduleMap.put(jobId, taskSchedule);
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
                jobScheduleMap.put(jobId, taskSchedule);
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
            // if all are offset dependency, we also should pass check
            long scheduleTime = DateTime.now().getMillis();
            for (TaskDependSchedule taskSchedule : jobScheduleMap.values()) {
                if (!taskSchedule.check(scheduleTime)) {
                    finishDependencies = false;
                    resetAllSelected();
                    break;
                }
            }
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
            DependencyExpression dependencyExpression = null;
            if (expression != null) {
                dependencyExpression = new TimeOffsetExpression(expression);
                dependSchedule.setDependencyExpression(dependencyExpression);
            }
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
        JobService jobService = Injectors.getInjector().getInstance(JobService.class);
        DependencyExpression dependencyExpression = null;
        Map<Long, JobDependencyEntry> dependencyMap = jobService.get(myJobId).getDependencies();
        if (dependencyMap != null && dependencyMap.containsKey(preJobId)) {
            dependencyExpression = dependencyMap.get(preJobId).getDependencyExpression();
        }
        TaskDependSchedule dependSchedule = new TaskDependSchedule(myJobId, preJobId, dependencyExpression);
        dependSchedule.init();

        return dependSchedule;
    }
}
