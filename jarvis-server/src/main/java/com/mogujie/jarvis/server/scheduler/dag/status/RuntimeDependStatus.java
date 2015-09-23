/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 上午11:09:57
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.mogujie.jarvis.server.scheduler.dag.strategy.CommonStrategy;

/**
 * @author guangming
 *
 */
public abstract class RuntimeDependStatus extends AbstractDependStatus {

    public RuntimeDependStatus() {}

    /**
     * @param myJobId
     * @param preJobId
     * @param commonStrategy
     */
    public RuntimeDependStatus(long myJobId, long preJobId, CommonStrategy commonStrategy) {
        super(myJobId, preJobId, commonStrategy);
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
    }

    /**
     * reset dependency status
     */
    public abstract void reset();

    @Override
    public boolean check() {
        boolean finishDependency = false;
        Map<Long, Boolean> taskStatusMap = getTaskStatusMap();
        CommonStrategy strategy = getCommonStrategy();
        if (taskStatusMap != null) {
            // 多个执行计划中任意一次成功即算成功
            if (strategy.equals(CommonStrategy.ANYONE)) {
                for (Map.Entry<Long, Boolean> entry : taskStatusMap.entrySet()) {
                    if (entry.getValue() == true) {
                        finishDependency = true;
                        break;
                    }
                }
            } else if (strategy.equals(CommonStrategy.LASTONE)) {
                // 多个执行计划中最后一次成功算成功
                Iterator<Entry<Long, Boolean>> it = taskStatusMap.entrySet().iterator();
                Map.Entry<Long, Boolean> entry = null;
                while (it.hasNext()) {
                    entry = it.next();
                }
                if (entry != null && entry.getValue() == true) {
                    finishDependency = true;
                }
            } else if (strategy.equals(CommonStrategy.ALL)) {
                // 多个执行计划中所有都成功才算成功
                if (!taskStatusMap.isEmpty()) {
                    finishDependency = true;
                    for (Map.Entry<Long, Boolean> entry : taskStatusMap.entrySet()) {
                        if (entry.getValue() == false) {
                            finishDependency = false;
                            break;
                        }
                    }
                }
            }
        }
        return finishDependency;
    }

    @Override
    public void setDependStatus(long taskId) {
        modifyDependStatus(taskId, true);
    }

    @Override
    public void resetDependStatus(long taskId) {
        modifyDependStatus(taskId, false);
    }

    protected abstract void modifyDependStatus(long taskId, boolean status);

    protected abstract Map<Long, Boolean> getTaskStatusMap();
}
