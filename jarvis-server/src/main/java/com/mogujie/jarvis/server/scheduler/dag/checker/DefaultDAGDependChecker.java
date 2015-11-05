/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月23日 下午3:11:42
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;


/**
 * @author guangming
 *
 */
public class DefaultDAGDependChecker extends DAGDependChecker {

    @Override
    protected AbstractTaskSchedule getDependStatus(long myJobId, long preJobId) {
        AbstractTaskSchedule dependStatus = null;
        try {
            dependStatus = TaskScheduleFactory.create(myJobId, preJobId);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }

        if (dependStatus != null) {
            dependStatus.init();
        }

        return dependStatus;
    }
}
