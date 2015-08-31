/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年7月9日 下午16:55:35
 */
package com.mogujie.jarvis.core.domain;

/**
 * @author guangming@mogujie.com
 *
 */
public enum WorkerStatus {
    ON(1), OFF(0);

    private int value;

    WorkerStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
