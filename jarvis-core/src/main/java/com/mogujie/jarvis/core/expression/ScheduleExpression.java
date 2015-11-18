/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年10月29日 下午10:36:27
 */

package com.mogujie.jarvis.core.expression;

import org.joda.time.DateTime;

public abstract class ScheduleExpression implements Expression {

    protected String expression;

    public ScheduleExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    abstract public DateTime getTimeBefore(DateTime dateTime);

    abstract public DateTime getTimeAfter(DateTime dateTime);
}
