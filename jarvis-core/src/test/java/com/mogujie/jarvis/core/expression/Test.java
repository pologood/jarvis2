package com.mogujie.jarvis.core.expression;

import org.joda.time.DateTime;

public class Test {

    public static void main(String[] args) {
        ScheduleExpression exp2 = new FixedDelayExpression("d('2015-01-01 12:00:00',5)");
        System.out.println(exp2.getTimeBefore(new DateTime(2015, 12, 8, 1, 2, 0)));
    }

}
