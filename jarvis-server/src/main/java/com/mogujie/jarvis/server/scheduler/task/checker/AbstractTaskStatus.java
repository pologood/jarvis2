/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月4日 下午8:33:17
 */

package com.mogujie.jarvis.server.scheduler.task.checker;

import com.mogujie.jarvis.server.scheduler.depend.strategy.CommonStrategy;



/**
 * @author guangming
 *
 */
public abstract class AbstractTaskStatus {
    private long myJobId;
    private long preJobId;
    private CommonStrategy commonStrategy;

    public AbstractTaskStatus(long myJobId, long preJobId, CommonStrategy commonStrategy) {
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

    public CommonStrategy getCommonStrategy() {
        return commonStrategy;
    }

    public void setCommonStrategy(CommonStrategy commonStrategy) {
        this.commonStrategy = commonStrategy;
    }

    public abstract boolean check();
}
