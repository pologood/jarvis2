/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年7月9日 下午16:55:35
 */
package com.mogujie.jarvis.core.domain;

/**
 * @author muming
 */
public enum WorkerGroupStatus {

    ENABLE(1),      //有效
    DISABLED(2);    //无效

    private int value;

    WorkerGroupStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Boolean isValid(int value) {
        WorkerGroupStatus[] values = WorkerGroupStatus.values();
        for (WorkerGroupStatus s : values) {
            if (s.getValue() == value) {
                return true;
            }
        }
        return false;
    }

    public static WorkerGroupStatus parseValue(int value) throws IllegalArgumentException{
        WorkerGroupStatus[] statusList = WorkerGroupStatus.values();
        for (WorkerGroupStatus s : statusList) {
            if (s.getValue() == value) {
                return s;
            }
        }
        throw new IllegalArgumentException("WorkerGroupStatus value is invalid. value:" + value);
    }

}
