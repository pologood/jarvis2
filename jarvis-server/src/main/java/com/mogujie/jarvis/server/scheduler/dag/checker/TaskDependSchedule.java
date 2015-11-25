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
import java.util.concurrent.ConcurrentSkipListSet;

import org.joda.time.DateTime;

import com.google.common.collect.Range;
import com.mogujie.jarvis.core.expression.TimeOffsetExpression;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class TaskDependSchedule {
    private long myJobId;
    private long preJobId;
    private String expression;
    private JobDependType dependType;

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

    public TaskDependSchedule() {}

    public TaskDependSchedule(long myJobId, long preJobId, String expression) {
        this.myJobId = myJobId;
        this.preJobId = preJobId;
        this.expression = expression;
        if (expression == null) {
            dependType = JobDependType.RUNTIME;
        } else if (expression.startsWith("c")) {
            dependType = JobDependType.CURRENT;
        } else {
            dependType = JobDependType.OFFSET;
        }
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

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
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
        if (dependType.equals(JobDependType.RUNTIME)) {
            // 默认实现至少有一个就通过依赖检查
            if (schedulingTasks.size() > 0) {
                selectedTasks.addAll(schedulingTasks);
                pass = true;
            }
        } else if (dependType.equals(JobDependType.CURRENT)) {
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
                pass = true;
            }
        } else {
            pass = true;
        }

        return pass;
    }

    /**
     * start schedule task
     */
    public void scheduleTask(long taskId, long scheduleTime) {
        if (!dependType.equals(JobDependType.OFFSET)) {
            ScheduleTask task = new ScheduleTask(taskId, scheduleTime);
            schedulingTasks.add(task);
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
        TaskService taskService = SpringContext.getBean(TaskService.class);
        DateTime now = DateTime.now();

        List<Task> tasks = null;
        if (dependType.equals(JobDependType.CURRENT)) {
            TimeOffsetExpression timeOffsetExpression = new TimeOffsetExpression(expression);
            Range<DateTime> range = timeOffsetExpression.getRange(now);
            tasks = taskService.getTasksBetween(preJobId, range.lowerEndpoint(),range.upperEndpoint());
        }

        if (tasks != null) {
            for (Task task : tasks) {
                schedulingTasks.add(new ScheduleTask(task.getTaskId(), task.getScheduleTime().getTime()));
            }
        }
     }
}
