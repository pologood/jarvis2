/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 上午11:10:47
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import com.mogujie.jarvis.server.scheduler.dag.strategy.AbstractOffsetDependStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.CommonStrategy;

/**
 * @author guangming
 *
 */
public class OffsetDependStatus extends AbstractDependStatus {

    private AbstractOffsetDependStrategy offsetDependStrategy;
    private int offset;

    public OffsetDependStatus(long myJobId, long preJobId, CommonStrategy commonStrategy,
            AbstractOffsetDependStrategy offsetDependStrategy, int offset) {
        super(myJobId, preJobId, commonStrategy);
        this.offsetDependStrategy = offsetDependStrategy;
        this.offset = offset;
    }

    @Override
    public boolean check() {
        return offsetDependStrategy.check(getPreJobId(), offset, getCommonStrategy());
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
