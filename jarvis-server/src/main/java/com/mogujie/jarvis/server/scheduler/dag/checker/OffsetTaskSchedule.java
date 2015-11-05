/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月5日 下午3:30:47
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import java.util.Set;

import com.google.common.collect.Sets;


/**
 * @author guangming
 *
 */
public class OffsetTaskSchedule extends AbstractTaskSchedule {

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public Set<Long> getSchedulingTaskIds() {
        Set<Long> dependTasks = Sets.newHashSet();
        dependTasks.add((long) 0);
        return dependTasks;
    }
}
