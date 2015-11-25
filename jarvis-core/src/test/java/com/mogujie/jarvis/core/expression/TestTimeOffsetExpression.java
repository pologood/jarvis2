package com.mogujie.jarvis.core.expression;

import com.google.common.collect.Range;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by muming on 15/11/25.
 */
public class TestTimeOffsetExpression {


    @Test
    public void test(){

        DateTime now = DateTime.now();
        Range<DateTime> range;

        TimeOffsetExpression cm = new TimeOffsetExpression("cm");
        Assert.assertTrue(cm.isValid());
        range = cm.getRange(now);
        Assert.assertNotNull(range);

        TimeOffsetExpression ch = new TimeOffsetExpression("ch");
        Assert.assertTrue(ch.isValid());
        range = ch.getRange(now);
        Assert.assertNotNull(range);

        TimeOffsetExpression cd = new TimeOffsetExpression("cd");
        Assert.assertTrue(cd.isValid());
        range = cd.getRange(now);
        Assert.assertNotNull(range);

        TimeOffsetExpression cM = new TimeOffsetExpression("cM");
        Assert.assertTrue(cM.isValid());
        range = cM.getRange(now);
        Assert.assertNotNull(range);

        TimeOffsetExpression cy = new TimeOffsetExpression("cy");
        Assert.assertTrue(cy.isValid());
        range = cy.getRange(now);
        Assert.assertNotNull(range);

        TimeOffsetExpression cw = new TimeOffsetExpression("cw");
        Assert.assertTrue(cw.isValid());
        range = cw.getRange(now);
        Assert.assertNotNull(range);

//        Range<DateTime> expected = new Range<>(now);
//        expected.
//        Assert.assertEquals(range,);

        TimeOffsetExpression expression2 = new TimeOffsetExpression("cd5");
        Assert.assertFalse(expression2.isValid());

    }



}
