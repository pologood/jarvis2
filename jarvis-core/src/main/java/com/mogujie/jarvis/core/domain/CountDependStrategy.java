/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月12日 下午4:56:39
 */
package com.mogujie.jarvis.core.domain;

/**
 * 任务的次数依赖策略
 *
 * @author muming
 *
 */
public enum CountDependStrategy {

    LAST(1),    //最后一个
    ANY(2),     //任意一个
    ALL(3);     //全部

    private int value;

    CountDependStrategy(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CountDependStrategy getInstance(int value) {
        CountDependStrategy[] statusList = CountDependStrategy.values();
        CountDependStrategy status = CountDependStrategy.LAST;
        for (CountDependStrategy s : statusList) {
            if (s.getValue() == value) {
                status = s;
                break;
            }
        }

        return status;
    }
}
