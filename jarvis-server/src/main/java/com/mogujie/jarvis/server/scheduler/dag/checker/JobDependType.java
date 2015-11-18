/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月18日 下午2:48:45
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

/**
 * @author guangming
 *
 */
public enum JobDependType {
    RUNTIME(0),
    CURRENT(1),
    OFFSET(2);

    private int value;

    JobDependType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
