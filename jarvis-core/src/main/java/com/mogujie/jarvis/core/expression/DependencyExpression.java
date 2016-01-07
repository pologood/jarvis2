/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年11月1日 下午2:22:49
 */

package com.mogujie.jarvis.core.expression;

import org.joda.time.DateTime;

import com.google.common.collect.Range;

public abstract class DependencyExpression implements Expression {
    protected String expression;

    public DependencyExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public String getExpression() {
        return expression;
    }

    abstract public Range<DateTime> getRange(DateTime dateTime);

    public Range<DateTime> getReverseRange(DateTime dateTime) {
        Range<DateTime> range = getRange(dateTime);
        if (range == null) {
            return null;
        }

        long t1 = dateTime.getMillis() - range.lowerEndpoint().getMillis();
        long t2 = range.upperEndpoint().getMillis() - dateTime.getMillis();
        return Range.range(dateTime.minus(t2), range.upperBoundType(), dateTime.plus(t1), range.lowerBoundType());
    }
}
