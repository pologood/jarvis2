/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月23日 下午4:41:12
 */

package com.mogujie.jarvis.server.scheduler.dag.strategy;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.mogujie.jarvis.core.common.util.ReflectionUtils;
import com.mogujie.jarvis.core.domain.Pair;

/**
 * @author guangming
 *
 */
public class OffsetStrategyFactory {
    public static String DAG_OFFSET_STRATEGY_KEY_PREFIX = "dag.offset.strategy.";

    public static Pair<AbstractOffsetStrategy, Integer> create(String offsetStrategyStr) {
        Pair<AbstractOffsetStrategy, Integer> strategyPair = null;
        if (offsetStrategyStr != null && !offsetStrategyStr.isEmpty()) {
            String offsetStrategyMap[] = offsetStrategyStr.split(":");
            String offsetStrategyKey = DAG_OFFSET_STRATEGY_KEY_PREFIX + offsetStrategyMap[0].trim().toLowerCase();
            Configuration conf = ConfigUtils.getServerConfig();
            String className = conf.getString(offsetStrategyKey);
            if (className != null) {
                int offsetValue = Integer.valueOf(offsetStrategyMap[1].trim());
                try {
                    AbstractOffsetStrategy offsetStrategy = ReflectionUtils.getInstanceByClassName(className);
                    strategyPair = new Pair<AbstractOffsetStrategy, Integer>(offsetStrategy, offsetValue);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }

        return strategyPair;
    }
}
