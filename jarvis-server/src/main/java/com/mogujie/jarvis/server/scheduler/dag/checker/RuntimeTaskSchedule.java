/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 上午11:09:57
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.joda.time.DateTime;

import com.google.common.collect.Range;
import com.mogujie.jarvis.core.expression.TimeOffsetExpression;
import com.mogujie.jarvis.dto.Task;
import com.mogujie.jarvis.server.scheduler.depend.strategy.CommonStrategy;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class RuntimeTaskSchedule extends AbstractTaskSchedule {

    // SortedSet<ScheduleTask>
    private SortedSet<ScheduleTask> schedulingTasks = new ConcurrentSkipListSet<>(
            new Comparator<ScheduleTask>() {
        @Override
        public int compare(ScheduleTask task1, ScheduleTask task2) {
            return (int)(task1.getScheduleTime() - task2.getScheduleTime());
        }
    });

    // List<ScheduleTask>
    private List<ScheduleTask> selectedTasks = new ArrayList<ScheduleTask>();
    protected String expression;

    public RuntimeTaskSchedule() {}

    /**
     * @param myJobId
     * @param preJobId
     * @param commonStrategy
     */
    public RuntimeTaskSchedule(long myJobId, long preJobId, CommonStrategy commonStrategy) {
        super(myJobId, preJobId, commonStrategy);
    }

    public RuntimeTaskSchedule(long myJobId, long preJobId, CommonStrategy commonStrategy, String expression) {
        this(myJobId, preJobId, commonStrategy);
        this.expression = expression;
    }

    @Override
    public void init() {
        loadSchedulingTasks();
    }

    @Override
    public void resetSchedule() {
        selectedTasks.clear();
    }

    @Override
    public void finishSchedule() {
        for (ScheduleTask task : selectedTasks) {
            schedulingTasks.remove(task);
        }
        selectedTasks.clear();
    }

    @Override
    public void scheduleTask(long taskId, long scheduleTime) {
        ScheduleTask task = new ScheduleTask(taskId, scheduleTime);
        schedulingTasks.add(task);
    }

    @Override
    public List<ScheduleTask> getSchedulingTasks() {
        return new ArrayList<ScheduleTask>(schedulingTasks);
    }

    @Override
    public List<ScheduleTask> getSelectedTasks() {
        return selectedTasks;
    }

    protected void loadSchedulingTasks() {
        TaskService taskService = SpringContext.getBean(TaskService.class);
        DateTime now = DateTime.now();

        List<Task> tasks = null;
        if (expression.equals("cd")) {
            // current day
            tasks = taskService.getTasksOfCurrentDay(getMyJobId(), now);
        } else if (expression.equals("ch")) {
            // current hour
            tasks = taskService.getTasksOfCurrentHour(getMyJobId(), now);
        }

        if (tasks != null) {
            for (Task task : tasks) {
                schedulingTasks.add(new ScheduleTask(task.getTaskId(), task.getScheduleTime().getTime()));
            }
        }
     }

    @Override
    public boolean check(long scheduleTime) {
        if (expression != null) {
            TimeOffsetExpression dependExpression = new TimeOffsetExpression(expression);
            DateTime scheduleDate = new DateTime(scheduleTime);
            Range<DateTime> range = dependExpression.getRange(scheduleDate);
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
                return true;
            }
        } else {
            // 默认实现至少有一个就通过依赖检查
            if (schedulingTasks.size() > 0) {
                selectedTasks.addAll(schedulingTasks);
                return true;
            }
        }
        return false;
    }

}
