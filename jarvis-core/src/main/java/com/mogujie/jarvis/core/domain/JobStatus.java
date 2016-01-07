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
 */
public enum JobStatus {

    ENABLE(1),      //启用（有效）
    DISABLE(2),     //禁用（失效）
    EXPIRED(3),     //过期
    DELETED(4),     //删除（垃圾箱）
    PAUSE(5);       //暂停

    private int value;

    JobStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static JobStatus parseValue(int value) {
        JobStatus[] statusList = JobStatus.values();
        JobStatus status = JobStatus.ENABLE;
        for (JobStatus s : statusList) {
            if (s.getValue() == value) {
                status = s;
                break;
            }
        }
        return status;
    }

    public static Boolean isValid(int value) {
        JobStatus[] values = JobStatus.values();
        for (JobStatus s : values) {
            if (s.getValue() == value) {
                return true;
            }
        }
        return false;
    }


}
