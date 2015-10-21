/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午1:52:49
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mogujie.jarvis.server.scheduler.dag.strategy.CommonStrategy;

/**
 * The implementation of AbstractDependStatus with cached map
 *
 * @author guangming
 *
 */
public class CachedDependStatus extends RuntimeDependStatus {
    // Map<taskId, status>
    protected Map<Long, Boolean> taskStatusMap = new ConcurrentHashMap<Long, Boolean>();

    public CachedDependStatus() {}

    /**
     * @param myJobId
     * @param preJobId
     * @param commonStrategy
     */
    public CachedDependStatus(long myJobId, long preJobId, CommonStrategy commonStrategy) {
        super(myJobId, preJobId, commonStrategy);
    }

    @Override
    protected void modifyDependStatus(long taskId, boolean status) {
        taskStatusMap.put(taskId, status);
    }

    @Override
    public void init() {
        this.taskStatusMap = loadTaskDependStatus();
    }

    @Override
    public void reset() {
        taskStatusMap.clear();
    }

    @Override
    protected Map<Long, Boolean> getTaskStatusMap() {
        return this.taskStatusMap;
    }

    protected Map<Long, Boolean> loadTaskDependStatus() {
        return new ConcurrentHashMap<Long, Boolean>();
    }
}
