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

/**
 * 
 *
 */
public abstract class DependencyExpression implements Expression {

    abstract public Range<DateTime> getRange(DateTime dateTime);
}
