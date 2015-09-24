/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月24日 下午4:49:46
 */

package com.mogujie.jarvis.core.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author guangming
 *
 */
public class TestCalendarUtil {
    @Test
    public void testGetDayBefore() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse("2015-09-24");
        Date lastDate = CalendarUtil.getDayBefore(date, 3);
        String lastDateStr = dateFormat.format(lastDate);
        Assert.assertEquals("2015-09-21", lastDateStr);
    }

    @Test
    public void testGetDayAfter() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse("2015-09-24");
        Date afterDay = CalendarUtil.getDayAfter(date, 3);
        String afterDayStr = dateFormat.format(afterDay);
        Assert.assertEquals("2015-09-27", afterDayStr);
    }

    @Test
    public void testMondayOfWeekBefore() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse("2015-09-24");
        // 上周一日期为"2015-09-14"
        Date last1Monday = CalendarUtil.getMondayOfWeekBefore(date, 1);
        String last1MondayStr = dateFormat.format(last1Monday);
        Assert.assertEquals("2015-09-14", last1MondayStr);

        // 上两周周一日期为"2015-09-07"
        Date last2Monday = CalendarUtil.getMondayOfWeekBefore(date, 2);
        String last2MondayStr = dateFormat.format(last2Monday);
        Assert.assertEquals("2015-09-07", last2MondayStr);
    }

    @Test
    public void testSundayOfWeekBefore() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse("2015-09-24");
        // 上周日日期为"2015-09-13"
        Date last1Sunday = CalendarUtil.getSundayOfWeekBefore(date, 1);
        String last1SundayStr = dateFormat.format(last1Sunday);
        Assert.assertEquals("2015-09-13", last1SundayStr);

        // 上两周周日日期为"2015-09-06"
        Date last2Sunday = CalendarUtil.getSundayOfWeekBefore(date, 2);
        String last2SundayStr = dateFormat.format(last2Sunday);
        Assert.assertEquals("2015-09-06", last2SundayStr);
    }

    @Test
    public void testFirstDayOfWeekBefore() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse("2015-09-24");
        // 上一周第一天日期为"2015-09-13"
        Date firstDayOfLastWeek = CalendarUtil.getFirstDayOfWeekBefore(date, 1);
        String firstDayOfLastWeekStr = dateFormat.format(firstDayOfLastWeek);
        Assert.assertEquals("2015-09-13", firstDayOfLastWeekStr);

        // 上两周第一天日期为"2015-09-06"
        Date firstDayOfLastTwoWeek = CalendarUtil.getFirstDayOfWeekBefore(date, 2);
        String firstDayOfLastTwoWeekStr = dateFormat.format(firstDayOfLastTwoWeek);
        Assert.assertEquals("2015-09-06", firstDayOfLastTwoWeekStr);
    }

    @Test
    public void testLastDayOfWeekBefore() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse("2015-09-24");
        // 上一周最后一天日期为"2015-09-19"
        Date lastDayOfLastWeek = CalendarUtil.getLastDayOfWeekBefore(date, 1);
        String lastDayOfLastWeekStr = dateFormat.format(lastDayOfLastWeek);
        Assert.assertEquals("2015-09-19", lastDayOfLastWeekStr);

        // 上两周第一天日期为"2015-09-12"
        Date lastDayOfLastTwoWeek = CalendarUtil.getLastDayOfWeekBefore(date, 2);
        String lastDayOfLastTwoWeekStr = dateFormat.format(lastDayOfLastTwoWeek);
        Assert.assertEquals("2015-09-12", lastDayOfLastTwoWeekStr);
    }

    @Test
    public void testFirstDayOfMonthBefore() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse("2015-09-24");
        // 上个月第一天日期为"2015-08-01"
        Date firstDayOfLastMonth = CalendarUtil.getFirstDayOfMonthBefore(date, 1);
        String firstDayOfLastMonthStr = dateFormat.format(firstDayOfLastMonth);
        Assert.assertEquals("2015-08-01", firstDayOfLastMonthStr);

        // 本月第一天日期为"2015-09-01"
        Date firstDayOfThisMonth = CalendarUtil.getFirstDayOfMonthBefore(date, 0);
        String firstDayOfThisMonthStr = dateFormat.format(firstDayOfThisMonth);
        Assert.assertEquals("2015-09-01", firstDayOfThisMonthStr);
    }

    @Test
    public void testLastDayOfMonthBefore() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse("2015-09-24");

        // 上个月最后一天日期为"2015-08-31"
        Date lastDayOfLastMonth = CalendarUtil.getLastDayOfMonthBefore(date, 1);
        String lastDayOfLastMonthStr = dateFormat.format(lastDayOfLastMonth);
        Assert.assertEquals("2015-08-31", lastDayOfLastMonthStr);

        // 上三个月最后一天日期为"2015-06-30"
        Date lastDayOfLastThreeMonth = CalendarUtil.getLastDayOfMonthBefore(date, 3);
        String lastDayOfLastThreeMonthStr = dateFormat.format(lastDayOfLastThreeMonth);
        Assert.assertEquals("2015-06-30", lastDayOfLastThreeMonthStr);
    }
}
