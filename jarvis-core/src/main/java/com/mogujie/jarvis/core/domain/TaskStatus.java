/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月12日 下午4:56:39
 */
package com.mogujie.jarvis.core.domain;

/**
 * @author muming
 *
 */
public enum TaskStatus {

    UNKNOWN(0), //未知
    WAITING(1), //等待（条件未满足）
    READY(2),   //准备（分发中）
    RUNNING(3), //执行中
    SUCCESS(4), //成功
    FAILED(5),  //失败
    KILLED(6);  //killed

    private int value;

    TaskStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TaskStatus getInstance(int value) {
        TaskStatus[] statusList = TaskStatus.values();
        TaskStatus status = TaskStatus.UNKNOWN;
        for (TaskStatus s : statusList) {
            if (s.getValue() == value) {
                status = s;
                break;
            }
        }

        return status;
    }
}
