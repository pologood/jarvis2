/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月18日 下午4:20:53
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import java.util.Map;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.server.domain.JobDependencyEntry;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class TaskScheduleFactory {
    public static final String TASK_SCHEDULE_KEY = "task.schedule.key";
    public static final String DEFAULT_TASK_SCHEDULE = TaskDependSchedule.class.getName();
    public static final String DUMMY_TASK_SCHEDULE = DummyTaskDependSchedule.class.getName();

    public static TaskDependSchedule create(long myJobId, long preJobId) {
        Configuration conf = ConfigUtils.getServerConfig();
        String className = conf.getString(TASK_SCHEDULE_KEY, DEFAULT_TASK_SCHEDULE);

        TaskDependSchedule dependSchedule = null;
        String offsetStrategy = null;
        if (className.equalsIgnoreCase(DEFAULT_TASK_SCHEDULE)) {
            JobService jobService = SpringContext.getBean(JobService.class);
            Map<Long, JobDependencyEntry> dependencyMap = jobService.get(myJobId).getDependencies();
            if (dependencyMap != null && dependencyMap.containsKey(preJobId)) {
                offsetStrategy = dependencyMap.get(preJobId).getDependencyExpression().getExpression();
            }
            dependSchedule = new TaskDependSchedule(myJobId, preJobId, offsetStrategy);
            dependSchedule.init();
        } else {
            dependSchedule = new DummyTaskDependSchedule(myJobId, preJobId, offsetStrategy);
        }

        return dependSchedule;
    }
}
