/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年11月1日 下午2:30:09
 */

package com.mogujie.jarvis.core.expression;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.util.DurationFieldTypes;

/**
 * Provides a parser and evaluator for timeOffset dependency expressions, such as "['yyyy-MM-dd 00:00:00',d(-1),d(1))".
 */
public class TimeOffsetExpression extends DependencyExpression {

    private int isValid;
    private char rangeStartFlag;
    private String format;
    private String startTimeOffset;
    private String endTimeOffset;
    private char rangeEndFlag;
    private String expressionFormula;

    private static final Map<Pattern, String> MAP = Maps.newHashMap();
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile(
            "([\\(\\[])'([y\\d]{4}-[M\\d]{2}-[d\\d]{2} [H\\d]{2}:[m\\d]{2}:[s\\d]{2})',((([smhdwMy]\\(((-?\\d+)|[smhHdeMy])\\))+),)?(([smhdwMy]\\(((-?\\d+)|[smhHdeMy])\\))*)([\\)\\]])");
    private static final Pattern SINGLE_OFFSET_PATTERN = Pattern.compile("([smhdwMy])\\(((-?\\d+)|[smhHdeMy])\\)");

    static {
        MAP.put(Pattern.compile("cm"), "['yyyy-MM-dd HH:mm:00',m(-1),m(1))");
        MAP.put(Pattern.compile("m\\((-?\\d+)\\)"), "('yyyy-MM-dd HH:mm:00',m(a)]");
        MAP.put(Pattern.compile("m\\((-?\\d+),(-?\\d+)\\)"), "('yyyy-MM-dd HH:mm:00',m(a),m(b)]");

        MAP.put(Pattern.compile("ch"), "['yyyy-MM-dd HH:00:00',h(-1),h(1))");
        MAP.put(Pattern.compile("h\\((-?\\d+)\\)"), "['yyyy-MM-dd HH:00:00',h(a))");
        MAP.put(Pattern.compile("h\\((-?\\d+),(-?\\d+)\\)"), "['yyyy-MM-dd HH:00:00',h(a),h(b))");

        MAP.put(Pattern.compile("cd"), "['yyyy-MM-dd 00:00:00',d(-1),d(1))");
        MAP.put(Pattern.compile("d\\((-?\\d+)\\)"), "['yyyy-MM-dd 00:00:00',d(a))");
        MAP.put(Pattern.compile("d\\((-?\\d+),(-?\\d+)\\)"), "['yyyy-MM-dd 00:00:00',d(a),d(b))");

        MAP.put(Pattern.compile("cM"), "['yyyy-MM-01 00:00:00',M(-1),M(1))");
        MAP.put(Pattern.compile("M\\((-?\\d+)\\)"), "['yyyy-MM-01 00:00:00',M(a))");
        MAP.put(Pattern.compile("M\\((-?\\d+),(-?\\d+)\\)"), "['yyyy-MM-01 00:00:00',M(a),M(b))");

        MAP.put(Pattern.compile("cy"), "['yyyy-01-01 00:00:00',y(-1),y(1))");
        MAP.put(Pattern.compile("y\\((-?\\d+)\\)"), "['yyyy-01-01 00:00:00',y(a))");
        MAP.put(Pattern.compile("y\\((-?\\d+),(-?\\d+)\\)"), "['yyyy-01-01 00:00:00',y(a),y(b))");

        MAP.put(Pattern.compile("cw"), "['yyyy-MM-dd 00:00:00',d(e),w(-1))");
        MAP.put(Pattern.compile("w\\((-?\\d+)\\)"), "['yyyy-MM-dd 00:00:00',d(e),w(a))");
        MAP.put(Pattern.compile("w\\((-?\\d+),(-?\\d+)\\)"), "['yyyy-MM-dd 00:00:00',d(e)w(a),w(b))");
    }

    public TimeOffsetExpression(String expression) {
        super(expression);
    }

    public static String convertAbbrExp(String abbrExp) {
        for (Entry<Pattern, String> entry : MAP.entrySet()) {
            Pattern pattern = entry.getKey();
            Matcher m = pattern.matcher(abbrExp);
            if (m.matches()) {
                int groupCount = m.groupCount();
                switch (groupCount) {
                    case 0:
                        return entry.getValue();
                    case 1:
                        return entry.getValue().replace("a", m.group(1));
                    case 2:
                        return entry.getValue().replace("a", m.group(1)).replace("b", m.group(2));
                    default:
                        break;
                }
            }
        }

        return abbrExp;
    }

    private static MutableDateTime convertSingleTimeOffset(MutableDateTime mutableDateTime, String exp) {
        Matcher m = SINGLE_OFFSET_PATTERN.matcher(exp);
        if (m.matches()) {
            char unit = m.group(1).charAt(0);
            String strValue = m.group(2);
            int value = 0;
            if (CharMatcher.DIGIT.matchesAllOf(strValue)) {
                value = Integer.parseInt(strValue);
            } else {
                value = Integer.parseInt(mutableDateTime.toString(strValue));
            }

            mutableDateTime.add(DurationFieldTypes.valueOf(unit), -value);
        }

        return mutableDateTime;
    }

    @Override
    public boolean isValid() {
        if (expression == null) {
            return false;
        }
        for (Entry<Pattern, String> entry : MAP.entrySet()) {
            Pattern pattern = entry.getKey();
            Matcher m = pattern.matcher(expression);
            if (m.matches()) {
                expressionFormula = convertAbbrExp(expression);
                break;
            }
        }
        if (expressionFormula == null) {
            return false;
        }
        Matcher m = EXPRESSION_PATTERN.matcher(expressionFormula);
        if (m.matches()) {

            rangeStartFlag = m.group(1).charAt(0);
            format = m.group(2);
            startTimeOffset = m.group(4);
            endTimeOffset = m.group(8);
            rangeEndFlag = m.group(12).charAt(0);

            try {
                DateTimeFormat.forPattern(JarvisConstants.DEFAULT_DATE_TIME_FORMAT).parseDateTime(DateTime.now().toString(format));
                isValid = 1;
                return true;
            } catch (IllegalFieldValueException e) {
                isValid = -1;
                return false;
            }
        } else {
            isValid = -1;
            return false;
        }
    }

    @Override
    public Range<DateTime> getRange(DateTime dateTime) {
        if (isValid > 0 || (isValid == 0 && isValid())) {
            DateTime currentDateTime = DateTimeFormat.forPattern(JarvisConstants.DEFAULT_DATE_TIME_FORMAT).parseDateTime(dateTime.toString(format));
            MutableDateTime startDateTime = currentDateTime.toMutableDateTime();
            if (startTimeOffset != null) {
                Matcher m = SINGLE_OFFSET_PATTERN.matcher(startTimeOffset);
                while (m.find()) {
                    startDateTime = convertSingleTimeOffset(startDateTime, m.group());
                }
            }

            MutableDateTime endDateTime = new MutableDateTime(startDateTime);
            Matcher m = SINGLE_OFFSET_PATTERN.matcher(endTimeOffset);
            while (m.find()) {
                endDateTime = convertSingleTimeOffset(endDateTime, m.group());
            }

            DateTime start = startDateTime.isBefore(endDateTime) ? startDateTime.toDateTime() : endDateTime.toDateTime();
            DateTime end = startDateTime.isAfter(endDateTime) ? startDateTime.toDateTime() : endDateTime.toDateTime();
            if (rangeStartFlag == '(' && rangeEndFlag == ')') {
                return Range.open(start, end);
            } else if (rangeStartFlag == '(' && rangeEndFlag == ']') {
                return Range.openClosed(start, end);
            } else if (rangeStartFlag == '[' && rangeEndFlag == ')') {
                return Range.closedOpen(start, end);
            } else {
                return Range.closed(start, end);
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return expression;
    }

    public String getExpressionFormula() {
        return expressionFormula;
    }
}
