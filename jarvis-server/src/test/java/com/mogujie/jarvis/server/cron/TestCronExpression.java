/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年1月14日 下午11:06:53
 */
package com.mogujie.jarvis.server.cron;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.joda.time.DateTime;
import org.junit.Test;

/**
 * @author wuya
 *
 */
public class TestCronExpression {

    @Test
    public void testMinute() throws ParseException {
        assertEquals(new CronExpression("0 3 * * * ? *").getTimeAfter(new DateTime(2015, 1, 14, 23, 1)), new DateTime(2015, 1, 14, 23, 3));
        assertEquals(new CronExpression("0 29 * * * ? *").getTimeAfter(new DateTime(2015, 1, 14, 22, 34)), new DateTime(2015, 1, 14, 23, 29));
        assertEquals(new CronExpression("0 29 * * * ? *").getTimeAfter(new DateTime(2015, 1, 14, 22, 29)), new DateTime(2015, 1, 14, 23, 29));
        assertEquals(new CronExpression("0 1 * * * ? *").getTimeAfter(new DateTime(2015, 1, 14, 22, 29)), new DateTime(2015, 1, 14, 23, 1));
        assertEquals(new CronExpression("0 59 * * * ? *").getTimeAfter(new DateTime(2015, 1, 14, 22, 29)), new DateTime(2015, 1, 14, 22, 59));
        assertEquals(new CronExpression("0 * * * * ? *").getTimeAfter(new DateTime(2015, 1, 14, 22, 29)), new DateTime(2015, 1, 14, 22, 30));
    }

    @Test
    public void check_all() throws ParseException {
        assertEquals(new CronExpression("* * * * * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 00, 01)), new DateTime(2012, 4, 10, 13, 00, 02));
        assertEquals(new CronExpression("* * * * * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 02)), new DateTime(2012, 4, 10, 13, 02, 01));
        assertEquals(new CronExpression("* * * * * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 59, 59)), new DateTime(2012, 4, 10, 14, 00));
    }

    @Test
    public void check_minute_number() throws ParseException {
        assertEquals(new CronExpression("0 3 * * * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 01)), new DateTime(2012, 4, 10, 13, 03));
        assertEquals(new CronExpression("0 3 * * * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 03)), new DateTime(2012, 4, 10, 14, 03));
    }

    @Test
    public void check_minute_increment() throws ParseException {
        assertEquals(new CronExpression("0 0/15 * * * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 00)), new DateTime(2012, 4, 10, 13, 15));
        assertEquals(new CronExpression("0 0/15 * * * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 15)), new DateTime(2012, 4, 10, 13, 30));
        assertEquals(new CronExpression("0 0/15 * * * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 30)), new DateTime(2012, 4, 10, 13, 45));
        assertEquals(new CronExpression("0 0/15 * * * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 45)), new DateTime(2012, 4, 10, 14, 00));
    }

    @Test
    public void check_minute_list() throws ParseException {
        assertEquals(new CronExpression("0, 7,19 * * * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 00)), new DateTime(2012, 4, 10, 13, 07));
        assertEquals(new CronExpression("0, 7,19 * * * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 07)), new DateTime(2012, 4, 10, 13, 19));
    }

    @Test
    public void check_hour_number() throws ParseException {
        assertEquals(new CronExpression("0 * 3 * * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 01)), new DateTime(2012, 4, 11, 03, 00));
        assertEquals(new CronExpression("0 * 3 * * ?").getTimeAfter(new DateTime(2012, 4, 11, 03, 00)), new DateTime(2012, 4, 11, 03, 01));
        assertEquals(new CronExpression("0 * 3 * * ?").getTimeAfter(new DateTime(2012, 4, 11, 03, 59)), new DateTime(2012, 4, 12, 03, 00));
    }

    @Test
    public void check_hour_increment() throws ParseException {
        assertEquals(new CronExpression("0 * 0/15 * * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 00)), new DateTime(2012, 4, 10, 15, 00));
        assertEquals(new CronExpression("0 * 0/15 * * ?").getTimeAfter(new DateTime(2012, 4, 10, 15, 00)), new DateTime(2012, 4, 10, 15, 01));
        assertEquals(new CronExpression("0 * 0/15 * * ?").getTimeAfter(new DateTime(2012, 4, 10, 15, 59)), new DateTime(2012, 4, 11, 00, 00));
        assertEquals(new CronExpression("0 * 0/15 * * ?").getTimeAfter(new DateTime(2012, 4, 11, 00, 00)), new DateTime(2012, 4, 11, 00, 01));
        assertEquals(new CronExpression("0 * 0/15 * * ?").getTimeAfter(new DateTime(2012, 4, 11, 15, 00)), new DateTime(2012, 4, 11, 15, 01));
    }

    @Test
    public void check_hour_list() throws ParseException {
        assertEquals(new CronExpression("0 * 7,19 * * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 00)), new DateTime(2012, 4, 10, 19, 00));
        assertEquals(new CronExpression("0 * 7,19 * * ?").getTimeAfter(new DateTime(2012, 4, 10, 19, 00)), new DateTime(2012, 4, 10, 19, 01));
        assertEquals(new CronExpression("0 * 7,19 * * ?").getTimeAfter(new DateTime(2012, 4, 10, 19, 59)), new DateTime(2012, 4, 11, 07, 00));
    }

    @Test
    public void check_dayOfMonth_number() throws ParseException {
        assertEquals(new CronExpression("0 * * 3 * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 00)), new DateTime(2012, 5, 03, 00, 00));
        assertEquals(new CronExpression("0 * * 3 * ?").getTimeAfter(new DateTime(2012, 5, 03, 00, 00)), new DateTime(2012, 5, 03, 00, 01));
        assertEquals(new CronExpression("0 * * 3 * ?").getTimeAfter(new DateTime(2012, 5, 03, 00, 59)), new DateTime(2012, 5, 03, 01, 00));
        assertEquals(new CronExpression("0 * * 3 * ?").getTimeAfter(new DateTime(2012, 5, 03, 23, 59)), new DateTime(2012, 6, 03, 00, 00));
    }

    @Test
    public void check_dayOfMonth_increment() throws ParseException {
        assertEquals(new CronExpression("0 0 0 1/15 * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 00)), new DateTime(2012, 4, 16, 00, 00));
        assertEquals(new CronExpression("0 0 0 1/15 * ?").getTimeAfter(new DateTime(2012, 4, 16, 00, 00)), new DateTime(2012, 5, 01, 00, 00));
        assertEquals(new CronExpression("0 0 0 1/15 * ?").getTimeAfter(new DateTime(2012, 4, 30, 00, 00)), new DateTime(2012, 5, 01, 00, 00));
        assertEquals(new CronExpression("0 0 0 1/15 * ?").getTimeAfter(new DateTime(2012, 5, 01, 00, 00)), new DateTime(2012, 5, 16, 00, 00));
    }

    @Test
    public void check_dayOfMonth_list() throws ParseException {
        assertEquals(new CronExpression("0 0 0 7,19 * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 00)), new DateTime(2012, 4, 19, 00, 00));
        assertEquals(new CronExpression("0 0 0 7,19 * ?").getTimeAfter(new DateTime(2012, 4, 19, 00, 00)), new DateTime(2012, 5, 07, 00, 00));
        assertEquals(new CronExpression("0 0 0 7,19 * ?").getTimeAfter(new DateTime(2012, 5, 07, 00, 00)), new DateTime(2012, 5, 19, 00, 00));
        assertEquals(new CronExpression("0 0 0 7,19 * ?").getTimeAfter(new DateTime(2012, 5, 30, 00, 00)), new DateTime(2012, 6, 07, 00, 00));
    }

    @Test
    public void check_dayOfMonth_last() throws ParseException {
        assertEquals(new CronExpression("0 0 0 L * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 00)), new DateTime(2012, 4, 30, 00, 00));
        assertEquals(new CronExpression("0 0 0 L * ?").getTimeAfter(new DateTime(2012, 2, 12, 00, 00)), new DateTime(2012, 2, 29, 00, 00));
    }

    @Test
    public void check_dayOfMonth_number_last_L() throws ParseException {
        assertEquals(new CronExpression("0 0 0 3L * ?").getTimeAfter(new DateTime(2012, 4, 10, 13, 00)), new DateTime(2012, 4, 30 - 2, 00, 00));
        assertEquals(new CronExpression("0 0 0 3L * ?").getTimeAfter(new DateTime(2012, 2, 12, 00, 00)), new DateTime(2012, 2, 29 - 2, 00, 00));
    }

    @Test
    public void check_dayOfMonth_closest_weekday_W() throws ParseException {
        // 9 - is weekday in may
        assertEquals(new CronExpression("0 0 0 9W * ?").getTimeAfter(new DateTime(2012, 5, 2, 00, 00)), new DateTime(2012, 5, 9, 00, 00));

        // 9 - is weekday in may
        assertEquals(new CronExpression("0 0 0 9W * ?").getTimeAfter(new DateTime(2012, 5, 8, 00, 00)), new DateTime(2012, 5, 9, 00, 00));

        // 9 - saturday, friday closest weekday in june
        assertEquals(new CronExpression("0 0 0 9W * ?").getTimeAfter(new DateTime(2012, 5, 9, 00, 00)), new DateTime(2012, 6, 8, 00, 00));

        // 9 - sunday, monday closest weekday in september
        assertEquals(new CronExpression("0 0 0 9W * ?").getTimeAfter(new DateTime(2012, 9, 1, 00, 00)), new DateTime(2012, 9, 10, 00, 00));
    }

    @Test(expected = ParseException.class)
    public void check_dayOfMonth_invalid_modifier() throws ParseException {
        new CronExpression("0 0 0 9X * ?").getTimeAfter(new DateTime());
    }

    @Test(expected = ParseException.class)
    public void check_dayOfMonth_invalid_increment_modifier() throws ParseException {
        new CronExpression("0 0 0 9#2 * ?").getTimeAfter(new DateTime());
    }

    @Test
    public void check_month_number() throws ParseException {
        assertEquals(new CronExpression("0 0 0 1 5 ?").getTimeAfter(new DateTime(2012, 2, 12, 00, 00)), new DateTime(2012, 5, 1, 00, 00));
    }

    @Test
    public void check_month_increment() throws ParseException {
        assertEquals(new CronExpression("0 0 0 1 5/2 ?").getTimeAfter(new DateTime(2012, 2, 12, 00, 00)), new DateTime(2012, 5, 1, 00, 00));
        assertEquals(new CronExpression("0 0 0 1 5/2 ?").getTimeAfter(new DateTime(2012, 5, 1, 00, 00)), new DateTime(2012, 7, 1, 00, 00));
        assertEquals(new CronExpression("0 0 0 1 5/10 ?").getTimeAfter(new DateTime(2012, 5, 1, 00, 00)), new DateTime(2013, 5, 1, 00, 00));
    }

    @Test
    public void check_month_list() throws ParseException {
        assertEquals(new CronExpression("0 0 0 1 3,7,12 ?").getTimeAfter(new DateTime(2012, 2, 12, 00, 00)), new DateTime(2012, 3, 1, 00, 00));
        assertEquals(new CronExpression("0 0 0 1 3,7,12 ?").getTimeAfter(new DateTime(2012, 3, 1, 00, 00)), new DateTime(2012, 7, 1, 00, 00));
        assertEquals(new CronExpression("0 0 0 1 3,7,12 ?").getTimeAfter(new DateTime(2012, 7, 1, 00, 00)), new DateTime(2012, 12, 1, 00, 00));
    }

    @Test(expected = ParseException.class)
    public void check_month_invalid_modifier() throws ParseException {
        new CronExpression("0 0 1 ? ?").getTimeAfter(new DateTime());
    }

    @Test
    public void check_dayOfWeek_number() throws ParseException {
        assertEquals(new CronExpression("0 0 0 ? * 3").getTimeAfter(new DateTime(2012, 4, 1, 00, 00)), new DateTime(2012, 4, 4, 00, 00));
        assertEquals(new CronExpression("0 0 0 ? * 3").getTimeAfter(new DateTime(2012, 4, 4, 00, 00)), new DateTime(2012, 4, 11, 00, 00));
        assertEquals(new CronExpression("0 0 0 ? * 3").getTimeAfter(new DateTime(2012, 4, 12, 00, 00)), new DateTime(2012, 4, 18, 00, 00));
        assertEquals(new CronExpression("0 0 0 ? * 3").getTimeAfter(new DateTime(2012, 4, 18, 00, 00)), new DateTime(2012, 4, 25, 00, 00));
    }

    @Test
    public void check_dayOfWeek_increment() throws ParseException {
        assertEquals(new CronExpression("0 0 0 ? * 3/2").getTimeAfter(new DateTime(2012, 4, 1, 00, 00)), new DateTime(2012, 4, 4, 00, 00));
        assertEquals(new CronExpression("0 0 0 ? * 3/2").getTimeAfter(new DateTime(2012, 4, 4, 00, 00)), new DateTime(2012, 4, 6, 00, 00));
        assertEquals(new CronExpression("0 0 0 ? * 3/2").getTimeAfter(new DateTime(2012, 4, 6, 00, 00)), new DateTime(2012, 4, 8, 00, 00));
        assertEquals(new CronExpression("0 0 0 ? * 3/2").getTimeAfter(new DateTime(2012, 4, 8, 00, 00)), new DateTime(2012, 4, 11, 00, 00));
    }

    @Test
    public void check_dayOfWeek_list() throws ParseException {
        assertEquals(new CronExpression("0 0 0 ? * 1,5,7").getTimeAfter(new DateTime(2012, 4, 1, 00, 00)), new DateTime(2012, 4, 2, 00, 00));
        assertEquals(new CronExpression("0 0 0 ? * 1,5,7").getTimeAfter(new DateTime(2012, 4, 2, 00, 00)), new DateTime(2012, 4, 6, 00, 00));
        assertEquals(new CronExpression("0 0 0 ? * 1,5,7").getTimeAfter(new DateTime(2012, 4, 6, 00, 00)), new DateTime(2012, 4, 8, 00, 00));
    }

    @Test
    public void check_dayOfWeek_last_friday_in_month() throws ParseException {
        assertEquals(new CronExpression("0 0 0 ? * 5L").getTimeAfter(new DateTime(2012, 4, 1, 00, 00)), new DateTime(2012, 4, 27, 00, 00));
        assertEquals(new CronExpression("0 0 0 ? * 5L").getTimeAfter(new DateTime(2012, 4, 27, 00, 00)), new DateTime(2012, 5, 25, 00, 00));
        assertEquals(new CronExpression("0 0 0 ? * 5L").getTimeAfter(new DateTime(2012, 2, 6, 00, 00)), new DateTime(2012, 2, 24, 00, 00));
    }

    @Test(expected = ParseException.class)
    public void check_dayOfWeek_invalid_modifier() throws ParseException {
        new CronExpression("0 0 0 * * 5W").getTimeAfter(new DateTime());
    }

    @Test(expected = ParseException.class)
    public void check_dayOfWeek_invalid_increment_modifier() throws ParseException {
        new CronExpression("0 0 0 * * 5?3").getTimeAfter(new DateTime());
    }

    @Test
    public void check_dayOfWeek_shall_interpret_7_as_sunday() throws ParseException {
        assertEquals(new CronExpression("0 0 0 ? * 7").getTimeAfter(new DateTime(2012, 4, 1, 00, 00)), new DateTime(2012, 4, 8, 00, 00));
        assertEquals(new CronExpression("0 0 0 ? * 7L").getTimeAfter(new DateTime(2012, 4, 1, 00, 00)), new DateTime(2012, 4, 29, 00, 00));
        assertEquals(new CronExpression("0 0 0 ? * 7#2").getTimeAfter(new DateTime(2012, 4, 1, 00, 00)), new DateTime(2012, 4, 8, 00, 00));
    }

    @Test
    public void check_dayOfWeek_nth_friday_in_month() throws ParseException {
        assertEquals(new CronExpression("0 0 0 ? * 5#3").getTimeAfter(new DateTime(2012, 4, 1, 00, 00)), new DateTime(2012, 4, 20, 00, 00));
        assertEquals(new CronExpression("0 0 0 ? * 5#3").getTimeAfter(new DateTime(2012, 4, 20, 00, 00)), new DateTime(2012, 5, 18, 00, 00));
        assertEquals(new CronExpression("0 0 0 ? * 7#1").getTimeAfter(new DateTime(2012, 3, 30, 00, 00)), new DateTime(2012, 4, 1, 00, 00));
        assertEquals(new CronExpression("0 0 0 ? * 7#1").getTimeAfter(new DateTime(2012, 4, 1, 00, 00)), new DateTime(2012, 5, 6, 00, 00));
        assertEquals(new CronExpression("0 0 0 ? * 3#5").getTimeAfter(new DateTime(2012, 2, 6, 00, 00)), new DateTime(2012, 2, 29, 00, 00)); // leapday
    }

    @Test(expected = ParseException.class)
    public void shall_not_not_support_rolling_period() throws ParseException {
        new CronExpression("* * 5-1 * * ?").getTimeAfter(new DateTime());
    }
}
