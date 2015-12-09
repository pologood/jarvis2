/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年12月9日 上午11:58:52
 */

package com.mogujie.jarvis.core.expression;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Range;

public class TestTimeOffsetExpression {

    @Test
    public void testMinute() {
        DateTime dateTime = new DateTime(2015, 12, 13, 12, 34, 56);

        DependencyExpression exp1 = new TimeOffsetExpression("cm");
        Assert.assertTrue(exp1.isValid());
        Assert.assertEquals(exp1.getRange(dateTime), Range.closedOpen(new DateTime(2015, 12, 13, 12, 34, 0), new DateTime(2015, 12, 13, 12, 35, 0)));

        DependencyExpression exp2 = new TimeOffsetExpression("m(5)");
        Assert.assertTrue(exp2.isValid());
        Assert.assertEquals(exp2.getRange(dateTime), Range.openClosed(new DateTime(2015, 12, 13, 12, 29, 0), new DateTime(2015, 12, 13, 12, 34, 0)));

        DependencyExpression exp3 = new TimeOffsetExpression("m(5,10)");
        Assert.assertTrue(exp3.isValid());
        Assert.assertEquals(exp3.getRange(dateTime), Range.openClosed(new DateTime(2015, 12, 13, 12, 19, 0), new DateTime(2015, 12, 13, 12, 29, 0)));

    }

    @Test
    public void testHour() {
        DateTime dateTime = new DateTime(2015, 12, 13, 12, 34, 56);

        DependencyExpression exp1 = new TimeOffsetExpression("ch");
        Assert.assertTrue(exp1.isValid());
        Assert.assertEquals(exp1.getRange(dateTime), Range.closedOpen(new DateTime(2015, 12, 13, 12, 0, 0), new DateTime(2015, 12, 13, 13, 0, 0)));

        DependencyExpression exp2 = new TimeOffsetExpression("h(5)");
        Assert.assertTrue(exp2.isValid());
        Assert.assertEquals(exp2.getRange(dateTime), Range.closedOpen(new DateTime(2015, 12, 13, 7, 0, 0), new DateTime(2015, 12, 13, 12, 0, 0)));

        DependencyExpression exp3 = new TimeOffsetExpression("h(5,10)");
        Assert.assertTrue(exp3.isValid());
        Assert.assertEquals(exp3.getRange(dateTime), Range.closedOpen(new DateTime(2015, 12, 12, 21, 0, 0), new DateTime(2015, 12, 13, 7, 0, 0)));

    }

    @Test
    public void testDay() {
        DateTime dateTime = new DateTime(2015, 12, 13, 12, 34, 56);

        DependencyExpression exp1 = new TimeOffsetExpression("cd");
        Assert.assertTrue(exp1.isValid());
        Assert.assertEquals(exp1.getRange(dateTime), Range.closedOpen(new DateTime(2015, 12, 13, 0, 0, 0), new DateTime(2015, 12, 14, 0, 0, 0)));

        DependencyExpression exp2 = new TimeOffsetExpression("d(5)");
        Assert.assertTrue(exp2.isValid());
        Assert.assertEquals(exp2.getRange(dateTime), Range.closedOpen(new DateTime(2015, 12, 8, 0, 0, 0), new DateTime(2015, 12, 13, 0, 0, 0)));

        DependencyExpression exp3 = new TimeOffsetExpression("d(5,10)");
        Assert.assertTrue(exp3.isValid());
        Assert.assertEquals(exp3.getRange(dateTime), Range.closedOpen(new DateTime(2015, 11, 28, 0, 0, 0), new DateTime(2015, 12, 8, 0, 0, 0)));

    }

    @Test
    public void testMonth() {
        DateTime dateTime = new DateTime(2015, 12, 13, 12, 34, 56);

        DependencyExpression exp1 = new TimeOffsetExpression("cM");
        Assert.assertTrue(exp1.isValid());
        Assert.assertEquals(exp1.getRange(dateTime), Range.closedOpen(new DateTime(2015, 12, 1, 0, 0, 0), new DateTime(2016, 1, 1, 0, 0, 0)));

        DependencyExpression exp2 = new TimeOffsetExpression("M(5)");
        Assert.assertTrue(exp2.isValid());
        Assert.assertEquals(exp2.getRange(dateTime), Range.closedOpen(new DateTime(2015, 7, 1, 0, 0, 0), new DateTime(2015, 12, 1, 0, 0, 0)));

        DependencyExpression exp3 = new TimeOffsetExpression("M(5,10)");
        Assert.assertTrue(exp3.isValid());
        Assert.assertEquals(exp3.getRange(dateTime), Range.closedOpen(new DateTime(2014, 9, 1, 0, 0, 0), new DateTime(2015, 7, 1, 0, 0, 0)));

    }

    @Test
    public void testYear() {
        DateTime dateTime = new DateTime(2015, 12, 13, 12, 34, 56);

        DependencyExpression exp1 = new TimeOffsetExpression("cy");
        Assert.assertTrue(exp1.isValid());
        Assert.assertEquals(exp1.getRange(dateTime), Range.closedOpen(new DateTime(2015, 1, 1, 0, 0, 0), new DateTime(2016, 1, 1, 0, 0, 0)));

        DependencyExpression exp2 = new TimeOffsetExpression("y(5)");
        Assert.assertTrue(exp2.isValid());
        Assert.assertEquals(exp2.getRange(dateTime), Range.closedOpen(new DateTime(2010, 1, 1, 0, 0, 0), new DateTime(2015, 1, 1, 0, 0, 0)));

        DependencyExpression exp3 = new TimeOffsetExpression("y(5,10)");
        Assert.assertTrue(exp3.isValid());
        Assert.assertEquals(exp3.getRange(dateTime), Range.closedOpen(new DateTime(2000, 1, 1, 0, 0, 0), new DateTime(2010, 1, 1, 0, 0, 0)));

    }

    @Test
    public void testWeek() {
        DateTime dateTime = new DateTime(2015, 12, 13, 12, 34, 56);

        DependencyExpression exp1 = new TimeOffsetExpression("cw");
        Assert.assertTrue(exp1.isValid());
        Assert.assertEquals(exp1.getRange(dateTime), Range.closedOpen(new DateTime(2015, 12, 6, 0, 0, 0), new DateTime(2015, 12, 13, 0, 0, 0)));

        DependencyExpression exp2 = new TimeOffsetExpression("w(5)");
        Assert.assertTrue(exp2.isValid());
        Assert.assertEquals(exp2.getRange(dateTime), Range.closedOpen(new DateTime(2015, 11, 1, 0, 0, 0), new DateTime(2015, 12, 6, 0, 0, 0)));

        DependencyExpression exp3 = new TimeOffsetExpression("w(5,10)");
        Assert.assertTrue(exp3.isValid());
        Assert.assertEquals(exp3.getRange(dateTime), Range.closedOpen(new DateTime(2015, 8, 23, 0, 0, 0), new DateTime(2015, 11, 1, 0, 0, 0)));

    }
}
