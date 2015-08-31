/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午4:00:33
 */

package com.mogujie.jarvis.server;

import com.mogujie.jarvis.server.domain.WorkerInfo;

/**
 * @author wuya
 *
 */
public class RoundRobinWorkerSelector implements WorkerSelector {

    @Override
    public synchronized WorkerInfo select(int workerGroupId) {
        // TODO RoundRobinWorkerSelector
        return null;
    }

}
