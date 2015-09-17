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
public enum CrontabType {

    POSITIVE(1), //正向
    NEGATIVE(2); //反向

    private int value;

    CrontabType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CrontabType getInstance(int value) {

        CrontabType[] all = CrontabType.values();
        CrontabType select = CrontabType.POSITIVE;
        for (CrontabType s : all) {
            if (s.getValue() == value) {
                select = s;
                break;
            }
        }

        return select;
    }
}
