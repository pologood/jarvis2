/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月23日 下午4:35:51
 */

package com.mogujie.jarvis.server.scheduler.dag;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.server.scheduler.dag.strategy.AbstractOffsetStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.OffsetDayStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.OffsetMonthStrategy;
import com.mogujie.jarvis.server.scheduler.dag.strategy.OffsetStrategyFactory;
import com.mogujie.jarvis.server.scheduler.dag.strategy.OffsetWeekStrategy;

/**
 * @author guangming
 *
 */
public class TestOffsetStrategy {
    private static Configuration conf = ConfigUtils.getServerConfig();

    @BeforeClass
    public static void setup() {
        conf.setProperty(OffsetStrategyFactory.DAG_OFFSET_STRATEGY_KEY_PREFIX + "lastday",
                OffsetDayStrategy.class.getName());
        conf.setProperty(OffsetStrategyFactory.DAG_OFFSET_STRATEGY_KEY_PREFIX + "lastweek",
                OffsetWeekStrategy.class.getName());
        conf.setProperty(OffsetStrategyFactory.DAG_OFFSET_STRATEGY_KEY_PREFIX + "lastmonth",
                OffsetMonthStrategy.class.getName());
    }

    @Test
    public void testOffsetDayStrategy() {
        String offsetStrategyStr = "lastday:3";
        Pair<AbstractOffsetStrategy, Integer> pair = OffsetStrategyFactory.create(offsetStrategyStr);
        Assert.assertEquals(OffsetDayStrategy.class.getName(),
                pair.getFirst().getClass().getName());
        Assert.assertEquals(3, (int)pair.getSecond());
    }

    @Test
    public void testOffsetWeekStrategy() {
        String offsetStrategyStr = " lastweek:12 ";
        Pair<AbstractOffsetStrategy, Integer> pair = OffsetStrategyFactory.create(offsetStrategyStr);
        Assert.assertEquals(OffsetWeekStrategy.class.getName(),
                pair.getFirst().getClass().getName());
        Assert.assertEquals(12, (int)pair.getSecond());
    }

    @Test
    public void testOffsetMonthStrategy() {
        String offsetStrategyStr = " LastMonth:10";
        Pair<AbstractOffsetStrategy, Integer> pair = OffsetStrategyFactory.create(offsetStrategyStr);
        Assert.assertEquals(OffsetMonthStrategy.class.getName(),
                pair.getFirst().getClass().getName());
        Assert.assertEquals(10, (int)pair.getSecond());
    }

}
