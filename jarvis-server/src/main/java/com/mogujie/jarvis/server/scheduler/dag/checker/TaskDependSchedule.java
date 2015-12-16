/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月18日 上午11:56:07
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.http.annotation.NotThreadSafe;
import org.joda.time.DateTime;

import com.google.common.collect.Range;
import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.service.TaskService;

/**
 * 任务A对任务B的依赖检查器。
 * 内部SortedSet<ScheduleTask> schedulingTasks维护当前正在调度的所有前置task
 * 内部List<ScheduleTask> selectedTasks维护每次依赖检查时已选择的前置task，如果依赖检查通过或失败，都会重置。
 * TaskDependSchedule不是线程安全的，但是当前外部都是同步调用，不会有问题。
 *
 * @author guangming
 *
 */
@NotThreadSafe
public class TaskDependSchedule {
    private long myJobId;
    private long preJobId;
    private DependencyExpression dependencyExpression;

    // SortedSet<ScheduleTask>
    private SortedSet<ScheduleTask> schedulingTasks = new TreeSet<ScheduleTask>(new Comparator<ScheduleTask>() {
        @Override
        public int compare(ScheduleTask task1, ScheduleTask task2) {
            return Long.compare(task1.getScheduleTime(), task2.getScheduleTime());
        }
    });

    // List<ScheduleTask>
    private List<ScheduleTask> selectedTasks = new ArrayList<ScheduleTask>();

    public TaskDependSchedule() {
    }

    public TaskDependSchedule(long myJobId, long preJobId, DependencyExpression dependencyExpression) {
        this.myJobId = myJobId;
        this.preJobId = preJobId;
        this.dependencyExpression = dependencyExpression;
    }

    public long getMyJobId() {
        return myJobId;
    }

    public void setMyjobId(long jobId) {
        this.myJobId = jobId;
    }

    public long getPreJobId() {
        return preJobId;
    }

    public void setPreJobId(long preJobId) {
        this.preJobId = preJobId;
    }

    public DependencyExpression getDependencyExpression() {
        return dependencyExpression;
    }

    public void setDependencyExpression(DependencyExpression dependencyExpression) {
        this.dependencyExpression = dependencyExpression;
    }

    /**
     * init
     */
    public void init() {
        loadSchedulingTasks();
    }

    /**
     * reset selected
     */
    public void resetSelected() {
        selectedTasks.clear();
    }

    /**
     * finish schedule
     */
    public void finishSchedule() {
        for (ScheduleTask task : selectedTasks) {
            schedulingTasks.remove(task);
        }
        selectedTasks.clear();
    }

    /**
     * check dependency
     */
    public boolean check(long scheduleTime) {
        boolean pass = false;

        // runtime
        if (dependencyExpression == null) {
            // 默认实现至少有一个就通过依赖检查
            if (schedulingTasks.size() > 0) {
                selectedTasks.addAll(schedulingTasks);
                pass = true;
            }
        } else {
            // offset
            DateTime scheduleDate = new DateTime(scheduleTime);
            Range<DateTime> range = dependencyExpression.getRange(scheduleDate);
            DateTime startTime = range.lowerEndpoint();
            DateTime endTime = range.upperEndpoint();

            if (scheduleDate.isAfter(endTime)) {
                // 对过去的依赖
                schedulingTasks.clear();
                selectedTasks.clear();
                pass = true;
            } else if (scheduleDate.isBefore(startTime)) {
                // 对未来的依赖，当前不支持
                schedulingTasks.clear();
                selectedTasks.clear();
                pass = false;
            } else {
                // 对当前周期的依赖，比如当天
                for (ScheduleTask task : schedulingTasks) {
                    long theTaskScheduleTime = task.getScheduleTime();
                    DateTime theScheduleDate = new DateTime(theTaskScheduleTime);
                    if (range.contains(theScheduleDate)) {
                        selectedTasks.add(task);
                    } else {
                        break;
                    }
                }
                if (selectedTasks.size() > 0) {
                    pass = true;
                }
            }
        }

        return pass;
    }

    /**
     * start schedule task
     */
    public void scheduleTask(long taskId, long scheduleTime) {
        // 如果是runtime
        if (dependencyExpression == null) {
            ScheduleTask task = new ScheduleTask(taskId, scheduleTime);
            schedulingTasks.add(task);
        } else {
            DateTime scheduleDate = new DateTime(scheduleTime);
            Range<DateTime> range = dependencyExpression.getRange(scheduleDate);
            // 或者current
            if (range.contains(scheduleDate)) {
                ScheduleTask task = new ScheduleTask(taskId, scheduleTime);
                schedulingTasks.add(task);
            }
        }
    }

    /**
     * get scheduling tasks
     */
    public List<ScheduleTask> getSchedulingTasks() {
        return new ArrayList<ScheduleTask>(schedulingTasks);
    }

    /**
     * get selected tasks
     */
    public List<ScheduleTask> getSelectedTasks() {
        return selectedTasks;
    }

    protected void loadSchedulingTasks() {
        TaskService taskService = Injectors.getInjector().getInstance(TaskService.class);
        DateTime now = DateTime.now();

        if (dependencyExpression != null) {
            Range<DateTime> range = dependencyExpression.getRange(now);
            // 如果是对当前周期的依赖，需要重新load进来
            if (range.contains(now)) {
                List<Task> tasks = taskService.getTasksBetween(preJobId, range);
                for (Task task : tasks) {
                    schedulingTasks.add(new ScheduleTask(task.getTaskId(), task.getScheduleTime().getTime()));
                }
            }
        }
    }
}
