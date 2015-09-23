/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月21日 上午10:23:11
 */

package com.mogujie.jarvis.server.scheduler.dag.strategy;

/**
 * @author guangming
 *
 */
public enum OffsetStrategyEnum {
    LASTDAY("lastday", OffsetDayDependStrategy.class.getName()),
    LASTWEEK("lastweek", OffsetWeekDependStrategy.class.getName()),
    LASKMONTH("lastmonth", OffsetMonthDependStrategy.class.getName());

    String key;
    String value;

    OffsetStrategyEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static OffsetStrategyEnum getInstance(String key) {
        OffsetStrategyEnum[] strategyList = OffsetStrategyEnum.values();
        OffsetStrategyEnum strategy = OffsetStrategyEnum.LASTDAY;
        for (OffsetStrategyEnum os : strategyList) {
            if (os.getKey().equals(key)) {
                strategy = os;
                break;
            }
        }
        return strategy;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
