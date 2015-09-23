/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月23日 下午3:11:42
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import com.mogujie.jarvis.server.scheduler.dag.status.AbstractDependStatus;
import com.mogujie.jarvis.server.scheduler.dag.status.DependStatusFactory;

/**
 * @author guangming
 *
 */
public class DefaultDAGDependChecker extends DAGDependChecker {

    @Override
    protected AbstractDependStatus getDependStatus(long myJobId, long preJobId) {
        AbstractDependStatus dependStatus = null;
        try {
            dependStatus = DependStatusFactory.create(myJobId, preJobId);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }

        if (dependStatus != null) {
            dependStatus.init();
        }

        return dependStatus;
    }
}
