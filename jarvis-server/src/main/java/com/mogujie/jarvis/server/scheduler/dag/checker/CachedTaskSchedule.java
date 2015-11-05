/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午1:52:49
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.mogujie.jarvis.server.scheduler.depend.strategy.CommonStrategy;

/**
 * The implementation of AbstractDependStatus with cached map
 *
 * @author guangming
 *
 */
public class CachedTaskSchedule extends RuntimeTaskSchedule {

    // List<ScheduleTask>
    protected List<ScheduleTask> schedulingTasks = new ArrayList<ScheduleTask>();

    public CachedTaskSchedule() {}

    /**
     * @param myJobId
     * @param preJobId
     * @param commonStrategy
     */
    public CachedTaskSchedule(long myJobId, long preJobId, CommonStrategy commonStrategy) {
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
    public Set<Long> getSchedulingTaskIds() {
        Set<Long> scheduingTaskIds = Sets.newHashSet();
        for (ScheduleTask task : schedulingTasks) {
            scheduingTaskIds.add(task.getTaskId());
        }
        return scheduingTaskIds;
    }

    @Override
    public List<ScheduleTask> getSchedulingTasks() {
        return schedulingTasks;
    }

    protected List<ScheduleTask> loadSchedulingTasks() {
        return new ArrayList<ScheduleTask>();
    }
}
