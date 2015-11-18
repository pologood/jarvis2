/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 下午2:39:43
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import com.mogujie.jarvis.server.domain.JobDependencyEntry;
import com.mogujie.jarvis.server.scheduler.depend.strategy.CommonStrategy;
import com.mogujie.jarvis.server.service.JobDependService;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class TaskScheduleFactory {

    public static AbstractTaskSchedule create(long myJobId, long preJobId) throws ClassNotFoundException {
        AbstractTaskSchedule dependSchedule = null;
        JobDependService jobDependService = SpringContext.getBean(JobDependService.class);
        JobService jobService = SpringContext.getBean(JobService.class);

        if (jobService != null) {
            JobDependencyEntry dependencyEntry = jobService.get(myJobId).getDependencies().get(preJobId);
            String offsetStrategy = dependencyEntry.getDependencyExpression().getExpression();
            CommonStrategy commonStrategy = CommonStrategy.getInstance(dependencyEntry.getDependencyStrategyExpression().getExpression());

            if (offsetStrategy == null) {
                dependSchedule = new RuntimeTaskSchedule(myJobId, preJobId, commonStrategy);
            } else if (offsetStrategy.startsWith("c")) {
                // current day/hour/...
                dependSchedule = new RuntimeTaskSchedule(myJobId, preJobId, commonStrategy, offsetStrategy);
            } else {
                dependSchedule = new OffsetTaskSchedule();
            }
        }

        return dependSchedule;
    }
}
