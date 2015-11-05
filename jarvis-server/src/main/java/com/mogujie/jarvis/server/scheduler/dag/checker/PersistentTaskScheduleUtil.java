/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月11日 上午10:09:18
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import java.util.ArrayList;
import java.util.List;

import com.mogujie.jarvis.dto.TaskSchedule;
import com.mogujie.jarvis.server.service.TaskScheduleService;

/**
 * @author guangming
 *
 */
public class PersistentTaskScheduleUtil {

    public static void scheduleTask(long myJobId, long preJobId, long preTaskId,
            long scheduleTime, TaskScheduleService service) {
        service.create(myJobId, preJobId, preTaskId, scheduleTime);
    }

    public static List<ScheduleTask> getSchedulingTasks(long myJobId, long preJobId, TaskScheduleService service) {
        List<TaskSchedule> records = service.getRecordsByPreJobId(myJobId, preJobId);
        List<ScheduleTask> schedulingTasks = new ArrayList<ScheduleTask>();
        for (TaskSchedule record : records) {
            long preTaskId = record.getPreTaskId();
            long scheduleTime = record.getScheduleTime();
            schedulingTasks.add(new ScheduleTask(preTaskId, scheduleTime));
        }
        return schedulingTasks;
    }
}
