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
    LASTDAY(OffsetDayDependStrategy.class.getName()),
    LASTWEEK(OffsetWeekDependStrategy.class.getName()),
    LASKMONTH(OffsetMonthDependStrategy.class.getName());

    String value;

    OffsetStrategyEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
