/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年1月4日 上午10:45:50
 */

package com.mogujie.jarvis.server.scheduler.time;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author guangming
 *
 */
public class TestTimePlan {
    private TimePlan plan = TimePlan.INSTANCE;

    @After
    public void tearDown() {
        plan.clear();
    }

    @Test
    public void testRemovePlan() {
        TimePlanEntry entry1 = new TimePlanEntry(1, new DateTime(1000));
        TimePlanEntry entry2 = new TimePlanEntry(2, new DateTime(2000));
        TimePlanEntry entry3 = new TimePlanEntry(3, new DateTime(3000), 3);

        plan.addPlan(entry2);
        plan.addPlan(entry3);
        plan.addPlan(entry1);
        Assert.assertEquals(3, plan.getPlan().size());

        plan.removePlan(new TimePlanEntry(100, new DateTime(2000)));
        Assert.assertEquals(3, plan.getPlan().size());

        plan.removePlan(new TimePlanEntry(2, new DateTime(2000)));
        Assert.assertEquals(2, plan.getPlan().size());

        plan.removePlan(new TimePlanEntry(0, null, 3));
        Assert.assertEquals(1, plan.getPlan().size());
    }
}
