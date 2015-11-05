/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 下午2:39:43
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.core.util.ReflectionUtils;
import com.mogujie.jarvis.dto.JobDepend;
import com.mogujie.jarvis.server.service.JobDependService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class TaskScheduleFactory {
    public static final String TASK_DEPEND_SCHEDULE_KEY = "task.schedule";
    public static final String DEFAULT_TASK_DEPEND_SCHEDULE = CachedPersistentTaskSchedule.class.getName();

    public static AbstractTaskSchedule create(long myJobId, long preJobId) throws ClassNotFoundException {
        AbstractTaskSchedule dependSchedule = null;
        JobDependService jobDependService = SpringContext.getBean(JobDependService.class);
        if (jobDependService != null) {
            JobDepend jobDepend = jobDependService.getRecord(myJobId, preJobId);
            if (jobDepend != null && jobDepend.getOffsetStrategy() != null && jobDepend.getOffsetStrategy() != "") {
                dependSchedule = new OffsetTaskSchedule();
            } else {
                Configuration conf = ConfigUtils.getServerConfig();
                String className = conf.getString(TASK_DEPEND_SCHEDULE_KEY, DEFAULT_TASK_DEPEND_SCHEDULE);
                dependSchedule = ReflectionUtils.getInstanceByClassName(className);
                dependSchedule.setMyjobId(myJobId);
                dependSchedule.setPreJobId(preJobId);
            }
        }

        return dependSchedule;
    }
}
