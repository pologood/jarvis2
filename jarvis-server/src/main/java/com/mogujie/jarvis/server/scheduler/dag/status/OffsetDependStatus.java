/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 上午11:10:47
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import com.mogujie.jarvis.server.domain.JobKey;
import com.mogujie.jarvis.server.scheduler.dag.strategy.AbstractOffsetStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.CommonStrategy;

/**
 * @author guangming
 *
 */
public class OffsetDependStatus extends AbstractDependStatus {

    private AbstractOffsetStrategy offsetDependStrategy;
    private int offset;

    public OffsetDependStatus(JobKey myJobKey, JobKey preJobKey, CommonStrategy commonStrategy,
            AbstractOffsetStrategy offsetDependStrategy, int offset) {
        super(myJobKey, preJobKey, commonStrategy);
        this.offsetDependStrategy = offsetDependStrategy;
        this.offset = offset;
    }

    @Override
    public boolean check() {
        return offsetDependStrategy.check(getPreJobKey().getJobId(), offset, getCommonStrategy());
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
}
