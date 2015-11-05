/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 上午10:43:20
 */

package com.mogujie.jarvis.server.scheduler.depend.strategy;


/**
 * @author guangming
 *
 */
public enum CommonStrategy {
    ALL(0),        // 依赖全部成功
    LASTONE(1),    // 依赖最后一次成功
    ANYONE(2);     // 依赖任何一次成功

    private int value;

    CommonStrategy(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CommonStrategy getInstance(int value) {
        CommonStrategy[] strategyList = CommonStrategy.values();
        CommonStrategy strategy = CommonStrategy.ALL;
        for (CommonStrategy cs : strategyList) {
            if (cs.getValue() == value) {
                strategy = cs;
                break;
            }
        }
        return strategy;
    }
}
