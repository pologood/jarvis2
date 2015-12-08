/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年12月8日 上午11:12:49
 */

package com.mogujie.jarvis.core.expression;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class TestFixedRateExpression {

    @Test
    public void testSecond() {
        ScheduleExpression exp1 = new FixedRateExpression("s(5)");
        Assert.assertTrue(exp1.isValid());
        Assert.assertEquals(exp1.getTimeBefore(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 8, 1, 1, 55));
        Assert.assertEquals(exp1.getTimeAfter(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 8, 1, 2, 5));

        Assert.assertTrue(!new FixedRateExpression("s(-3)").isValid());
        Assert.assertTrue(!new FixedRateExpression("s(0)").isValid());

        ScheduleExpression exp2 = new FixedRateExpression("s('2015-01-01 12:00:00',10)");
        Assert.assertTrue(exp2.isValid());
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 8, 1, 1, 50));
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2015, 1, 1, 12, 0, 0)), new DateTime(2015, 1, 1, 11, 59, 50));
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 8, 1, 2, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 8, 1, 2, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2015, 1, 1, 12, 0, 0)), new DateTime(2015, 1, 1, 12, 0, 10));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 8, 1, 2, 10));

        ScheduleExpression exp3 = new FixedRateExpression("s('2030-01-01 12:00:00',10)");
        Assert.assertTrue(exp3.isValid());
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 8, 1, 1, 50));
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2030, 1, 1, 12, 0, 0)), new DateTime(2030, 1, 1, 11, 59, 50));
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2031, 12, 8, 1, 2, 0)), new DateTime(2031, 12, 8, 1, 2, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 8, 1, 2, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2030, 1, 1, 12, 0, 0)), new DateTime(2030, 1, 1, 12, 0, 10));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2031, 12, 8, 1, 2, 0)), new DateTime(2031, 12, 8, 1, 2, 10));

        Assert.assertTrue(!new FixedRateExpression("s('2015-01-01 12:00:66',10)").isValid());
        Assert.assertTrue(!new FixedRateExpression("s('2015-01-01 12:00',10)").isValid());
    }

    @Test
    public void testMinute() {
        ScheduleExpression exp1 = new FixedRateExpression("m(5)");
        Assert.assertTrue(exp1.isValid());
        Assert.assertEquals(exp1.getTimeBefore(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 8, 0, 57, 0));
        Assert.assertEquals(exp1.getTimeAfter(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 8, 1, 7, 0));

        Assert.assertTrue(!new FixedRateExpression("m(-3)").isValid());
        Assert.assertTrue(!new FixedRateExpression("m(0)").isValid());

        ScheduleExpression exp2 = new FixedRateExpression("m('2015-01-01 12:00:00',5)");
        Assert.assertTrue(exp2.isValid());
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 8, 1, 0, 0));
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2015, 1, 1, 12, 0, 0)), new DateTime(2015, 1, 1, 11, 55, 0));
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 8, 1, 0, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 8, 1, 5, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2015, 1, 1, 12, 0, 0)), new DateTime(2015, 1, 1, 12, 5, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 8, 1, 5, 0));

        ScheduleExpression exp3 = new FixedRateExpression("m('2030-01-01 12:00:00',5)");
        Assert.assertTrue(exp3.isValid());
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 8, 1, 0, 0));
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2030, 1, 1, 12, 0, 0)), new DateTime(2030, 1, 1, 11, 55, 0));
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2031, 12, 8, 1, 2, 0)), new DateTime(2031, 12, 8, 1, 0, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 8, 1, 5, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2030, 1, 1, 12, 0, 0)), new DateTime(2030, 1, 1, 12, 5, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2031, 12, 8, 1, 2, 0)), new DateTime(2031, 12, 8, 1, 5, 0));

        Assert.assertTrue(!new FixedRateExpression("m('2015-01-01 12:00:66',10)").isValid());
        Assert.assertTrue(!new FixedRateExpression("m('2015-01-01 12:00',10)").isValid());
    }

    @Test
    public void testHour() {
        ScheduleExpression exp1 = new FixedRateExpression("h(5)");
        Assert.assertTrue(exp1.isValid());
        Assert.assertEquals(exp1.getTimeBefore(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 7, 20, 2, 0));
        Assert.assertEquals(exp1.getTimeAfter(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 8, 6, 2, 0));

        Assert.assertTrue(!new FixedRateExpression("h(-3)").isValid());
        Assert.assertTrue(!new FixedRateExpression("h(0)").isValid());

        ScheduleExpression exp2 = new FixedRateExpression("h('2015-01-01 12:00:00',5)");
        Assert.assertTrue(exp2.isValid());
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 7, 21, 0, 0));
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2015, 1, 1, 12, 0, 0)), new DateTime(2015, 1, 1, 7, 0, 0));
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 7, 22, 0, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 8, 2, 0, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2015, 1, 1, 12, 0, 0)), new DateTime(2015, 1, 1, 17, 0, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 8, 3, 0, 0));

        ScheduleExpression exp3 = new FixedRateExpression("h('2030-01-01 12:00:00',5)");
        Assert.assertTrue(exp3.isValid());
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 7, 22, 0, 0));
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2030, 1, 1, 12, 0, 0)), new DateTime(2030, 1, 1, 7, 0, 0));
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2031, 12, 8, 1, 2, 0)), new DateTime(2031, 12, 7, 22, 0, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 8, 3, 0, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2030, 1, 1, 12, 0, 0)), new DateTime(2030, 1, 1, 17, 0, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2031, 12, 8, 1, 2, 0)), new DateTime(2031, 12, 8, 3, 0, 0));

        Assert.assertTrue(!new FixedRateExpression("h('2015-01-01 12:00:66',10)").isValid());
        Assert.assertTrue(!new FixedRateExpression("h('2015-01-01 12:00',10)").isValid());
    }

    @Test
    public void testDay() {
        ScheduleExpression exp1 = new FixedRateExpression("d(5)");
        Assert.assertTrue(exp1.isValid());
        Assert.assertEquals(exp1.getTimeBefore(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 3, 1, 2, 0));
        Assert.assertEquals(exp1.getTimeAfter(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 13, 1, 2, 0));

        Assert.assertTrue(!new FixedRateExpression("d(-3)").isValid());
        Assert.assertTrue(!new FixedRateExpression("d(0)").isValid());

        ScheduleExpression exp2 = new FixedRateExpression("d('2015-01-01 12:00:00',5)");
        Assert.assertTrue(exp2.isValid());
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 3, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2015, 1, 1, 12, 0, 0)), new DateTime(2014, 12, 27, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 7, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 8, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2015, 1, 1, 12, 0, 0)), new DateTime(2015, 1, 6, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 12, 12, 0, 0));

        ScheduleExpression exp3 = new FixedRateExpression("d('2030-01-01 12:00:00',5)");
        Assert.assertTrue(exp3.isValid());
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 7, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2030, 1, 1, 12, 0, 0)), new DateTime(2029, 12, 27, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2031, 12, 8, 1, 2, 0)), new DateTime(2031, 12, 7, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 12, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2030, 1, 1, 12, 0, 0)), new DateTime(2030, 1, 6, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2031, 12, 8, 1, 2, 0)), new DateTime(2031, 12, 12, 12, 0, 0));

        Assert.assertTrue(!new FixedRateExpression("d('2015-01-01 12:00:66',10)").isValid());
        Assert.assertTrue(!new FixedRateExpression("d('2015-01-01 12:00',10)").isValid());
    }

    @Test
    public void testMonth() {
        ScheduleExpression exp1 = new FixedRateExpression("M(5)");
        Assert.assertTrue(exp1.isValid());
        Assert.assertEquals(exp1.getTimeBefore(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 7, 8, 1, 2, 0));
        Assert.assertEquals(exp1.getTimeAfter(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2016, 5, 8, 1, 2, 0));

        Assert.assertTrue(!new FixedRateExpression("M(-3)").isValid());
        Assert.assertTrue(!new FixedRateExpression("M(0)").isValid());

        ScheduleExpression exp2 = new FixedRateExpression("M('2015-01-01 12:00:00',5)");
        Assert.assertTrue(exp2.isValid());
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 8, 1, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2015, 1, 1, 12, 0, 0)), new DateTime(2014, 8, 1, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 11, 1, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2015, 1, 1, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2015, 1, 1, 12, 0, 0)), new DateTime(2015, 6, 1, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2016, 4, 1, 12, 0, 0));

        ScheduleExpression exp3 = new FixedRateExpression("M('2030-01-01 12:00:00',5)");
        Assert.assertTrue(exp3.isValid());
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 8, 1, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2030, 1, 1, 12, 0, 0)), new DateTime(2029, 8, 1, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2031, 12, 8, 1, 2, 0)), new DateTime(2031, 9, 1, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2015, 1, 1, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2030, 1, 1, 12, 0, 0)), new DateTime(2030, 6, 1, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2031, 12, 8, 1, 2, 0)), new DateTime(2032, 2, 1, 12, 0, 0));

        Assert.assertTrue(!new FixedRateExpression("M('2015-01-01 12:00:66',10)").isValid());
        Assert.assertTrue(!new FixedRateExpression("M('2015-01-01 12:00',10)").isValid());
    }

    @Test
    public void testYear() {
        ScheduleExpression exp1 = new FixedRateExpression("y(5)");
        Assert.assertTrue(exp1.isValid());
        Assert.assertEquals(exp1.getTimeBefore(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2010, 12, 8, 1, 2, 0));
        Assert.assertEquals(exp1.getTimeAfter(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2020, 12, 8, 1, 2, 0));

        Assert.assertTrue(!new FixedRateExpression("y(-3)").isValid());
        Assert.assertTrue(!new FixedRateExpression("y(0)").isValid());

        ScheduleExpression exp2 = new FixedRateExpression("y('2015-01-01 12:00:00',5)");
        Assert.assertTrue(exp2.isValid());
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2010, 1, 1, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2015, 1, 1, 12, 0, 0)), new DateTime(2010, 1, 1, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2010, 1, 1, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2020, 1, 1, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2015, 1, 1, 12, 0, 0)), new DateTime(2020, 1, 1, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2020, 1, 1, 12, 0, 0));

        ScheduleExpression exp3 = new FixedRateExpression("y('2030-01-01 12:00:00',5)");
        Assert.assertTrue(exp3.isValid());
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2010, 1, 1, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2030, 1, 1, 12, 0, 0)), new DateTime(2025, 1, 1, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2031, 12, 8, 1, 2, 0)), new DateTime(2030, 1, 1, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2015, 1, 1, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2030, 1, 1, 12, 0, 0)), new DateTime(2035, 1, 1, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2031, 12, 8, 1, 2, 0)), new DateTime(2035, 1, 1, 12, 0, 0));

        Assert.assertTrue(!new FixedRateExpression("y('2015-01-01 12:00:66',10)").isValid());
        Assert.assertTrue(!new FixedRateExpression("y('2015-01-01 12:00',10)").isValid());
    }

    @Test
    public void testWeek() {
        ScheduleExpression exp1 = new FixedRateExpression("w(1)");
        Assert.assertTrue(exp1.isValid());
        Assert.assertEquals(exp1.getTimeBefore(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 1, 1, 2, 0));
        Assert.assertEquals(exp1.getTimeAfter(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 15, 1, 2, 0));

        Assert.assertTrue(!new FixedRateExpression("w(-3)").isValid());
        Assert.assertTrue(!new FixedRateExpression("w(0)").isValid());

        ScheduleExpression exp2 = new FixedRateExpression("w('2015-01-01 12:00:00',1)");
        Assert.assertTrue(exp2.isValid());
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 2, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2015, 1, 1, 12, 0, 0)), new DateTime(2014, 12, 25, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeBefore(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 3, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 9, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2015, 1, 1, 12, 0, 0)), new DateTime(2015, 1, 8, 12, 0, 0));
        Assert.assertEquals(exp2.getTimeAfter(new DateTime(2015, 12, 8, 1, 2, 0)), new DateTime(2015, 12, 10, 12, 0, 0));

        ScheduleExpression exp3 = new FixedRateExpression("w('2030-01-01 12:00:00',1)");
        Assert.assertTrue(exp3.isValid());
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 7, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2030, 1, 1, 12, 0, 0)), new DateTime(2029, 12, 25, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeBefore(new DateTime(2031, 12, 8, 1, 2, 0)), new DateTime(2031, 12, 2, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2014, 10, 8, 1, 2, 0)), new DateTime(2014, 10, 14, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2030, 1, 1, 12, 0, 0)), new DateTime(2030, 1, 8, 12, 0, 0));
        Assert.assertEquals(exp3.getTimeAfter(new DateTime(2031, 12, 8, 1, 2, 0)), new DateTime(2031, 12, 9, 12, 0, 0));

        Assert.assertTrue(!new FixedRateExpression("w('2015-01-01 12:00:66',10)").isValid());
        Assert.assertTrue(!new FixedRateExpression("w('2015-01-01 12:00',10)").isValid());
    }
}
