/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午2:13:48
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import java.util.List;

import com.mogujie.jarvis.server.service.TaskScheduleService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class CachedPersistentTaskSchedule extends CachedTaskSchedule {
    private TaskScheduleService scheduleService;

    public CachedPersistentTaskSchedule() {
        scheduleService = SpringContext.getBean(TaskScheduleService.class);
    }

    @Override
    public void resetSchedule() {
        super.resetSchedule();
        scheduleService.clearByPreJobId(getMyJobId(), getPreJobId());
    }

    @Override
    public void scheduleTask(long taskId, long scheduleTime) {
        super.scheduleTask(taskId, scheduleTime);
        PersistentTaskScheduleUtil.scheduleTask(getMyJobId(), getPreJobId(),
                taskId, scheduleTime, scheduleService);
    }

    @Override
    protected List<ScheduleTask> loadSchedulingTasks() {
        return PersistentTaskScheduleUtil.getSchedulingTasks(getMyJobId(), getPreJobId(), scheduleService);
    }
}
