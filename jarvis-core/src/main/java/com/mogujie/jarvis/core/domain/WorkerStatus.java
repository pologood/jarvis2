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

    public static Boolean isValid(int value) {
        WorkerStatus[] values = WorkerStatus.values();
        for (WorkerStatus s : values) {
            if (s.getValue() == value) {
                return true;
            }
        }
        return false;
    }

    public static WorkerStatus getInstance(int value) {
        WorkerStatus[] statusList = WorkerStatus.values();
        WorkerStatus status = WorkerStatus.ONLINE;
        for (WorkerStatus s : statusList) {
            if (s.getValue() == value) {
                status = s;
                break;
            }
        }
        return status;
    }


}
