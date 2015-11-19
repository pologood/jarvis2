/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月4日 下午8:33:17
 */

package com.mogujie.jarvis.server.scheduler.task.checker;

import java.util.List;

import com.mogujie.jarvis.core.expression.DependencyStrategyExpression;



/**
 * @author guangming
 *
 */
public abstract class AbstractTaskStatus {
    private long myJobId;
    private long preJobId;
    private DependencyStrategyExpression commonStrategy;

    public AbstractTaskStatus(long myJobId, long preJobId, DependencyStrategyExpression commonStrategy) {
        this.myJobId = myJobId;
        this.preJobId = preJobId;
        this.commonStrategy = commonStrategy;
    }

    public long getMyJobId() {
        return myJobId;
    }

    public void setMyJobId(long myJobId) {
        this.myJobId = myJobId;
    }

    public long getPreJobId() {
        return preJobId;
    }

    public void setPreJobId(long preJobId) {
        this.preJobId = preJobId;
    }

    public DependencyStrategyExpression getCommonStrategy() {
        return commonStrategy;
    }

    public void setCommonStrategy(DependencyStrategyExpression commonStrategy) {
        this.commonStrategy = commonStrategy;
    }

    public boolean check() {
        return commonStrategy.check(getStatusList());
    }

    public abstract List<Long> getDependTaskIds();

    public abstract void setDependTaskIds(List<Long> dependTaskIds);

    protected abstract List<Boolean> getStatusList();
}
