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

import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.core.expression.TimeOffsetExpression;
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
    public static final String JOB_OFFSET_STRATEGY = "job.offset.strategy";

    public static TaskDependSchedule create(long myJobId, long preJobId) {
        Configuration conf = ConfigUtils.getServerConfig();
        String className = conf.getString(TASK_SCHEDULE_KEY, DEFAULT_TASK_SCHEDULE);

        TaskDependSchedule dependSchedule = null;
        DependencyExpression dependencyExpression = null;
        if (className.equalsIgnoreCase(DEFAULT_TASK_SCHEDULE)) {
            JobService jobService = SpringContext.getBean(JobService.class);
            Map<Long, JobDependencyEntry> dependencyMap = jobService.get(myJobId).getDependencies();
            if (dependencyMap != null && dependencyMap.containsKey(preJobId)) {
                dependencyExpression = dependencyMap.get(preJobId).getDependencyExpression();
            }
            dependSchedule = new TaskDependSchedule(myJobId, preJobId, dependencyExpression);
        } else {
            String offsetStrategy = conf.getString(JOB_OFFSET_STRATEGY);
            if (offsetStrategy != null) {
                dependencyExpression = new TimeOffsetExpression(offsetStrategy);
            }
            dependSchedule = new DummyTaskDependSchedule(myJobId, preJobId, dependencyExpression);
        }
        dependSchedule.init();

        return dependSchedule;
    }
}
