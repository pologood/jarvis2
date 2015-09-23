/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月23日 下午4:41:12
 */

package com.mogujie.jarvis.server.scheduler.dag.strategy;

import com.mogujie.jarvis.core.common.util.ReflectionUtils;
import com.mogujie.jarvis.core.domain.Pair;

/**
 * @author guangming
 *
 */
public class OffsetStrategyFactory {
    public static Pair<AbstractOffsetDependStrategy, Integer> create(String offsetStrategyStr) {
        Pair<AbstractOffsetDependStrategy, Integer> strategyPair = null;
        if (offsetStrategyStr != null && !offsetStrategyStr.isEmpty()) {
            String offsetStrategyMap[] = offsetStrategyStr.split(":");
            OffsetStrategyEnum offsetStrategyEnum = OffsetStrategyEnum.getInstance(
                    offsetStrategyMap[0].trim());
            if (offsetStrategyEnum != null) {
                String className = offsetStrategyEnum.getValue();
                int offsetValue = Integer.valueOf(offsetStrategyMap[1].trim());
                try {
                    AbstractOffsetDependStrategy offsetStrategy = ReflectionUtils.getInstanceByClassName(className);
                    strategyPair = new Pair<AbstractOffsetDependStrategy, Integer>(offsetStrategy, offsetValue);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }

        return strategyPair;
    }
}
