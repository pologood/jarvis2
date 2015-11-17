/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午1:52:49
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import com.mogujie.jarvis.server.scheduler.depend.strategy.CommonStrategy;

/**
 * The implementation of AbstractDependStatus with cached map
 *
 * @author guangming
 *
 */
public class CachedTaskSchedule extends RuntimeTaskSchedule {

    public CachedTaskSchedule() {}

    /**
     * @param myJobId
     * @param preJobId
     * @param commonStrategy
     */
    public CachedTaskSchedule(long myJobId, long preJobId, CommonStrategy commonStrategy) {
        super(myJobId, preJobId, commonStrategy);
    }

    @Override
    protected void loadSchedulingTasks() {
    }
}
