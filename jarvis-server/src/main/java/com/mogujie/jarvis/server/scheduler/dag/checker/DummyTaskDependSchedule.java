/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月18日 下午4:23:57
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

/**
 * @author guangming
 *
 */
public class DummyTaskDependSchedule extends TaskDependSchedule {
    /**
     * @param myJobId
     * @param preJobId
     * @param expression
     */
    public DummyTaskDependSchedule(long myJobId, long preJobId, String expression) {
        super(myJobId, preJobId, expression);
    }

    @Override
    protected void loadSchedulingTasks() {}
}
