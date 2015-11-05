/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月23日 下午3:10:58
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import com.mogujie.jarvis.server.scheduler.depend.strategy.CommonStrategy;

/**
 * DummyDAGDependChecker for testing
 *
 * @author guangming
 *
 */
public class DummyDAGDependChecker extends DAGDependChecker {

    @Override
    protected AbstractTaskSchedule getDependStatus(long myJobId, long preJobId) {
        return new CachedTaskSchedule(myJobId, preJobId, CommonStrategy.ALL);
    }
}
