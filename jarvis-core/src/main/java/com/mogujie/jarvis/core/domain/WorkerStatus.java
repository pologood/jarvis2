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

    OFFLINE(0), //下线
    ONLINE(1);  //上线

    private int value;

    WorkerStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}