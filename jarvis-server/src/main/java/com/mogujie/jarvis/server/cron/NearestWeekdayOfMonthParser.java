/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年1月13日 下午1:57:53
 */
package com.mogujie.jarvis.server.cron;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import com.google.common.collect.Range;

/**
 * @author wuya
 *
 */
public class NearestWeekdayOfMonthParser extends AbstractParser {

    private Set<Integer> set;
    private Set<Integer> resultSet;
    private Range<Integer> range;
    private DurationField type;
    private static final Pattern NEAREST_WEEKDAY_OF_MONTH_PATTERN = Pattern.compile("(\\d+)W");

    protected NearestWeekdayOfMonthParser(Range<Integer> range, DurationField type) {
        super(range, type);
        this.range = range;
        this.type = type;
    }

    @Override
    protected boolean matches(String cronFieldExp) throws ParseException {
        Matcher m = NEAREST_WEEKDAY_OF_MONTH_PATTERN.matcher(cronFieldExp);
        if (m.matches()) {
            int value = Integer.parseInt(m.group(1));
            if (range.contains(value)) {
                if (set == null) {
                    set = new HashSet<Integer>();
                }
                set.add(value);
                return true;
            } else {
                throw new ParseException(
                        String.format("Invalid value of %s: %s, out of range %s", type.name, cronFieldExp, range.toString().replace("‥", ", ")), -1);
            }
        }

        return false;
    }

    @Override
    protected Set<Integer> parse(DateTime dateTime) {
        MutableDateTime mdt = dateTime.dayOfMonth().withMaximumValue().toMutableDateTime();
        int maxDayOfMonth = mdt.getDayOfMonth();

        if (set != null) {
            if (resultSet == null) {
                resultSet = new HashSet<Integer>();
            }
            resultSet.clear();

            for (Integer value : set) {
                if (value <= maxDayOfMonth) {
                    mdt.setDayOfMonth(value);
                    if (mdt.getDayOfWeek() < 6) {
                        resultSet.add(mdt.getDayOfMonth());
                        continue;
                    }

                    if (value + 1 <= maxDayOfMonth) {
                        mdt.setDayOfMonth(value + 1);
                        if (mdt.getDayOfWeek() < 6) {
                            resultSet.add(mdt.getDayOfMonth());
                            continue;
                        }
                    }

                    if (value - 1 > 0) {
                        mdt.setDayOfMonth(value - 1);
                        if (mdt.getDayOfWeek() < 6) {
                            resultSet.add(mdt.getDayOfMonth());
                            continue;
                        }
                    }
                }
            }

            return resultSet;
        }

        return null;
    }

}
