/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 上午11:09:57
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import java.util.ArrayList;
import java.util.List;

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

    // List<ScheduleTask>
    protected List<ScheduleTask> schedulingTasks = new ArrayList<ScheduleTask>();
    protected String expression = "cd"; //TODO

    public RuntimeTaskSchedule() {}

    /**
     * @param myJobId
     * @param preJobId
     * @param commonStrategy
     */
    public RuntimeTaskSchedule(long myJobId, long preJobId, CommonStrategy commonStrategy) {
        super(myJobId, preJobId, commonStrategy);
    }

    @Override
    public void init() {
        this.schedulingTasks = loadSchedulingTasks();
    }

    @Override
    public void resetSchedule() {
        schedulingTasks.clear();
    }

    @Override
    public void scheduleTask(long taskId, long scheduleTime) {
        ScheduleTask task = new ScheduleTask(taskId, scheduleTime);
        schedulingTasks.add(task);
    }

    @Override
    public List<ScheduleTask> getSchedulingTasks() {
        return schedulingTasks;
    }

    protected List<ScheduleTask> loadSchedulingTasks() {
        TaskService taskService = SpringContext.getBean(TaskService.class);
        DateTime now = DateTime.now();

        List<Task> tasks = null;
        List<ScheduleTask> scheduleTasks = new ArrayList<ScheduleTask>();
        if (expression.equals("cd")) {
            // current day
            tasks = taskService.getTasksOfCurrentDay(getMyJobId(), now);
        } else if (expression.equals("ch")) {
            // current hour
            tasks = taskService.getTasksOfCurrentHour(getMyJobId(), now);
        }

        if (tasks != null) {
            for (Task task : tasks) {
                scheduleTasks.add(new ScheduleTask(task.getTaskId(), task.getScheduleTime().getTime()));
            }
        }
        return scheduleTasks;
     }

    @Override
    public boolean check() {
        List<ScheduleTask> schedulingTasks = getSchedulingTasks();
        if (expression != null) {
            TimeOffsetExpression dependExpression = new TimeOffsetExpression(expression);
            DateTime now = DateTime.now();
            Range<DateTime> range = dependExpression.getRange(now);
            for (ScheduleTask task : schedulingTasks) {
                long scheduleTime = task.getScheduleTime();
                DateTime scheduleDate = new DateTime(scheduleTime);
                if (range.contains(scheduleDate)) {
                    return true;
                }
            }
        } else {
            // 默认实现至少有一个就通过依赖检查
            if (schedulingTasks.size() > 0) {
                return true;
            }
        }
        return false;
    }

}
