/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 下午2:39:43
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import com.mogujie.jarvis.dto.JobDepend;
import com.mogujie.jarvis.server.scheduler.depend.strategy.CommonStrategy;
import com.mogujie.jarvis.server.service.JobDependService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class TaskScheduleFactory {
//    public static final String TASK_DEPEND_SCHEDULE_KEY = "task.schedule";
//    public static final String DEFAULT_TASK_DEPEND_SCHEDULE = RuntimeTaskSchedule.class.getName();

    public static AbstractTaskSchedule create(long myJobId, long preJobId) throws ClassNotFoundException {
        AbstractTaskSchedule dependSchedule = null;
        JobDependService jobDependService = SpringContext.getBean(JobDependService.class);
        if (jobDependService != null) {
            JobDepend jobDepend = jobDependService.getRecord(myJobId, preJobId);
            if (jobDepend != null && jobDepend.getOffsetStrategy() != null && jobDepend.getOffsetStrategy() != "") {
                dependSchedule = new OffsetTaskSchedule();
            } else {
                CommonStrategy commonStrategy = CommonStrategy.getInstance(jobDepend.getCommonStrategy());
                dependSchedule =  new RuntimeTaskSchedule(myJobId, preJobId, commonStrategy);
            }
        }

        return dependSchedule;
    }
}
