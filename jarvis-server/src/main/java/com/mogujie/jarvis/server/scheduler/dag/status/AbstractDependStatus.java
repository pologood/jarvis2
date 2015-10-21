/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午11:49:39
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import com.mogujie.jarvis.server.domain.JobKey;
import com.mogujie.jarvis.server.scheduler.dag.strategy.CommonStrategy;


/**
 * @author guangming
 *
 */
public abstract class AbstractDependStatus {

    private JobKey myJobKey;
    private JobKey preJobKey;
    private CommonStrategy commonStrategy;

    public AbstractDependStatus() {}

    public AbstractDependStatus(JobKey myJobKey, JobKey preJobKey, CommonStrategy commonStrategy) {
        this.myJobKey = myJobKey;
        this.preJobKey = preJobKey;
        this.commonStrategy = commonStrategy;
    }

    public JobKey getMyJobKey() {
        return myJobKey;
    }

    public void setMyJobKey(JobKey jobKey) {
        this.myJobKey = jobKey;
    }

    public JobKey getPreJobKey() {
        return preJobKey;
    }

    public void setPreJobKey(JobKey jobKey) {
        this.preJobKey = preJobKey;
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
    public void init() {}

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

}
