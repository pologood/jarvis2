/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月23日 下午4:35:51
 */

package com.mogujie.jarvis.server.scheduler.dag;

import org.junit.Assert;
import org.junit.Test;

import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.server.scheduler.dag.strategy.AbstractOffsetDependStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.OffsetDayDependStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.OffsetMonthDependStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.OffsetStrategyFactory;
import com.mogujie.jarvis.server.scheduler.dag.strategy.OffsetWeekDependStrategy;

/**
 * @author guangming
 *
 */
public class TestOffsetStrategy {
    @Test
    public void testOffsetDayStrategy() {
        String offsetStrategyStr = "lastday:3";
        Pair<AbstractOffsetDependStrategy, Integer> pair = OffsetStrategyFactory.create(offsetStrategyStr);
        Assert.assertEquals(OffsetDayDependStrategy.class.getName(),
                pair.getFirst().getClass().getName());
        Assert.assertEquals(3, (int)pair.getSecond());
    }

    @Test
    public void testOffsetWeekStrategy() {
        String offsetStrategyStr = " lastweek:12 ";
        Pair<AbstractOffsetDependStrategy, Integer> pair = OffsetStrategyFactory.create(offsetStrategyStr);
        Assert.assertEquals(OffsetWeekDependStrategy.class.getName(),
                pair.getFirst().getClass().getName());
        Assert.assertEquals(12, (int)pair.getSecond());
    }

    @Test
    public void testOffsetMonthStrategy() {
        String offsetStrategyStr = " LastMonth:10";
        Pair<AbstractOffsetDependStrategy, Integer> pair = OffsetStrategyFactory.create(offsetStrategyStr);
        Assert.assertEquals(OffsetMonthDependStrategy.class.getName(),
                pair.getFirst().getClass().getName());
        Assert.assertEquals(10, (int)pair.getSecond());
    }

}
