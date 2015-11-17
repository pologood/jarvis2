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

    public static AbstractTaskSchedule create(long myJobId, long preJobId) throws ClassNotFoundException {
        AbstractTaskSchedule dependSchedule = null;
        JobDependService jobDependService = SpringContext.getBean(JobDependService.class);
        if (jobDependService != null) {
            JobDepend jobDepend = jobDependService.getRecord(myJobId, preJobId);
            if (jobDepend != null) {
                String offsetStrategy = jobDepend.getOffsetStrategy();
                CommonStrategy commonStrategy = CommonStrategy.getInstance(jobDepend.getCommonStrategy());

                if (offsetStrategy == null) {
                    dependSchedule = new RuntimeTaskSchedule(myJobId, preJobId, commonStrategy);
                } else if (offsetStrategy.startsWith("c")) {
                    dependSchedule = new RuntimeTaskSchedule(myJobId, preJobId, commonStrategy, offsetStrategy);
                } else {
                    dependSchedule = new OffsetTaskSchedule();
                }
            }
        }

        return dependSchedule;
    }
}
