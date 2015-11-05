/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 上午11:10:47
 */
package com.mogujie.jarvis.server.scheduler.task.checker;

import com.mogujie.jarvis.server.scheduler.depend.strategy.AbstractOffsetStrategy;
import com.mogujie.jarvis.server.scheduler.depend.strategy.CommonStrategy;

/**
 * @author guangming
 *
 */
public class OffsetDependStatus extends AbstractTaskStatus {

    private AbstractOffsetStrategy offsetDependStrategy;
    private int offset;

    public OffsetDependStatus(long myJobId, long preJobId, CommonStrategy commonStrategy,
            AbstractOffsetStrategy offsetDependStrategy, int offset) {
        super(myJobId, preJobId, commonStrategy);
        this.offsetDependStrategy = offsetDependStrategy;
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public AbstractOffsetStrategy getOffsetDependStrategy() {
        return offsetDependStrategy;
    }

    public void setOffsetDependStrategy(AbstractOffsetStrategy offsetDependStrategy) {
        this.offsetDependStrategy = offsetDependStrategy;
    }

    @Override
    public boolean check() {
        return offsetDependStrategy.check(getPreJobId(), offset, getCommonStrategy());
    }
}
