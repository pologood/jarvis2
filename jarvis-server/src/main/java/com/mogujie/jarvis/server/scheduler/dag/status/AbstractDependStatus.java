/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:49:39
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import com.mogujie.jarvis.server.scheduler.dag.strategy.CommonStrategy;


/**
 * @author guangming
 *
 */
public abstract class AbstractDependStatus {

    private long myJobId;
    private long preJobId;
    private CommonStrategy commonStrategy;

    public AbstractDependStatus() {}

    public AbstractDependStatus(long myJobId, long preJobId, CommonStrategy commonStrategy) {
        this.myJobId = myJobId;
        this.preJobId = preJobId;
        this.commonStrategy = commonStrategy;
    }

    public long getMyJobId() {
        return myJobId;
    }

    public void setMyjobId(long jobId) {
        this.myJobId = jobId;
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

    /**
     * init
     */
    public abstract void init();

    /**
     * reset
     */
    public void reset() {}

    /**
     * check
     */
    public abstract boolean check();

    /**
     * update ready dependency job status to true
     */
    public void setDependStatus(long taskId) {}

    /**
     * update ready dependency job status to false
     */
    public void resetDependStatus(long taskId) {}

    /**
     * remove job dependency
     */
    public void removeDependency() {}
}
