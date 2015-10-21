/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月29日 下午5:04:00
 */

package com.mogujie.jarvis.server.domain;

import com.mogujie.jarvis.server.scheduler.dag.strategy.CommonStrategy;


/**
 * @author guangming
 *
 */
public class ModifyDependEntry {

    private ModifyOperation operation;
    private JobKey preJobKey;
    private int commonStrategy;
    private String offsetStrategy;

    public ModifyDependEntry(ModifyOperation operation, JobKey preJobKey) {
        this.operation = operation;
        this.preJobKey = preJobKey;
        this.commonStrategy = CommonStrategy.ALL.getValue();
    }

    public ModifyDependEntry(ModifyOperation operation, JobKey preJobKey,
            int commonStrategy, String offsetStrategy) {
        this.operation = operation;
        this.preJobKey = preJobKey;
        this.commonStrategy = commonStrategy;
        this.offsetStrategy = offsetStrategy;
    }

    public ModifyOperation getOperation() {
        return operation;
    }

    public void setOperation(ModifyOperation operation) {
        this.operation = operation;
    }

    public JobKey getPreJobKey() {
        return preJobKey;
    }

    public void setPreJobKey(JobKey preJobKey) {
        this.preJobKey = preJobKey;
    }

    public int getCommonStrategy() {
        return commonStrategy;
    }

    public void setCommonStrategy(int commonStrategy) {
        this.commonStrategy = commonStrategy;
    }

    public String getOffsetStrategy() {
        return offsetStrategy;
    }

    public void setOffsetStrategy(String offsetStrategy) {
        this.offsetStrategy = offsetStrategy;
    }

}
