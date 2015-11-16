/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午1:29:59
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import java.util.List;

import com.mogujie.jarvis.server.service.TaskScheduleService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * The implementation of AbstractDependStatus with mysql mapping
 *
 * @author guangming
 *
 */
public class PersistentTaskSchedule extends RuntimeTaskSchedule {
    private TaskScheduleService scheduleService;

    public PersistentTaskSchedule() {
        scheduleService = SpringContext.getBean(TaskScheduleService.class);
    }

    @Override
    public void resetSchedule() {
        scheduleService.clearByPreJobId(getMyJobId(), getPreJobId());
    }

    @Override
    public void scheduleTask(long taskId, long scheduleTime) {
        PersistentTaskScheduleUtil.scheduleTask(getMyJobId(), getPreJobId(),
                taskId, scheduleTime, scheduleService);
    }

    @Override
    public List<ScheduleTask> getSchedulingTasks() {
        return PersistentTaskScheduleUtil.getSchedulingTasks(getMyJobId(), getPreJobId(), scheduleService);
    }
}
