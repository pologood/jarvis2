/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 上午11:09:57
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import java.util.List;

import com.mogujie.jarvis.server.scheduler.depend.strategy.CommonStrategy;

/**
 * @author guangming
 *
 */
public abstract class RuntimeTaskSchedule extends AbstractTaskSchedule {

    public RuntimeTaskSchedule() {}

    /**
     * @param myJobId
     * @param preJobId
     * @param commonStrategy
     */
    public RuntimeTaskSchedule(long myJobId, long preJobId, CommonStrategy commonStrategy) {
        super(myJobId, preJobId, commonStrategy);
    }

    @Override
    public boolean check() {
        List<ScheduleTask> schedulingTasks = getSchedulingTasks();
        //TODO 默认实现至少有一个就通过依赖检查
        if (schedulingTasks.size() > 0) {
            return true;
        }
        return false;
    }

    protected abstract List<ScheduleTask> getSchedulingTasks();
}
