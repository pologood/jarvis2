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
public enum JobFlag {

    ENABLE(1), //启用（有效）
    DISABLE(2), //禁用（失效）
    DELETED(3),   //垃圾箱
    EXPIRED(4); //过期

    private int value;

    JobFlag(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static JobFlag getInstance(int value) {
        JobFlag[] statusList = JobFlag.values();
        JobFlag status = JobFlag.ENABLE;
        for (JobFlag s : statusList) {
            if (s.getValue() == value) {
                status = s;
                break;
            }
        }

        return status;
    }
}
