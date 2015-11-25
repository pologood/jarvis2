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

        TimeOffsetExpression expression = new TimeOffsetExpression("cd");
        Assert.assertTrue(expression.isValid());

        Range<DateTime> range = expression.getRange(DateTime.now());
        Assert.assertNotNull(range);

        TimeOffsetExpression expression2 = new TimeOffsetExpression("cd5");
        Assert.assertFalse(expression2.isValid());

    }



}
