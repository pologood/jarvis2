/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年10月29日 下午10:46:12
 */

package com.mogujie.jarvis.core.expression;


/**
 *
 *
 */
public enum ScheduleExpressionType {

    CRON(1),
    FIXED_RATE(2),
    FIXED_DELAY(3),
    ISO8601(4);

    private int value;

    ScheduleExpressionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ScheduleExpressionType getInstance(int value) {
        ScheduleExpressionType[] typeList = ScheduleExpressionType.values();
        ScheduleExpressionType type = ScheduleExpressionType.CRON;
        for (ScheduleExpressionType t : typeList) {
            if (t.getValue() == value) {
                type = t;
                break;
            }
        }
        return type;
    }

    public static Boolean isValid(int value) {
        ScheduleExpressionType[] values = ScheduleExpressionType.values();
        for (ScheduleExpressionType s : values) {
            if (s.getValue() == value) {
                return true;
            }
        }
        return false;
    }

}
