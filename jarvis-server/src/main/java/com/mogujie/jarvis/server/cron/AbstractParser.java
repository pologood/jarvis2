/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年1月13日 下午1:24:45
 */
package com.mogujie.jarvis.server.cron;

import java.text.ParseException;
import java.util.Set;

import org.joda.time.DateTime;

import com.google.common.collect.Range;

/**
 * @author wuya
 *
 */
public abstract class AbstractParser {

    protected enum DurationField {

        SECOND(0, "second"), MINUTE(1, "minute"), HOUR(2, "hour"), DAY_OF_MONTH(3, "day-of-month"), MONTH(4, "month"), DAY_OF_WEEK(5,
                "day-of-week"), YEAR(6, "year");

        final int index;
        final String name;

        DurationField(int index, String name) {
            this.index = index;
            this.name = name;
        }
    }

    protected AbstractParser(Range<Integer> range, DurationField type) {
    }

    abstract protected boolean matches(String cronFieldExp) throws ParseException;

    abstract protected Set<Integer> parse(DateTime dateTime);

}
