/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月7日 下午3:16:59
 */

package com.mogujie.jarvis.worker.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import com.google.common.base.Throwables;
import com.mogujie.jarvis.core.AbstractTask;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.util.ReflectionUtils;
import com.mogujie.jarvis.worker.strategy.AcceptanceStrategy;

public class TaskConfigUtils {

  private static XMLConfiguration config;

  static {
    try {
      config = new XMLConfiguration("job.xml");
    } catch (ConfigurationException e) {
      Throwables.propagate(e);
    }
  }

  public static Set<String> getJobStrategies() {
    Set<String> set = new HashSet<>();
    List<Object> lists = config.configurationAt(".strategies").getList("strategy");
    for (Object object : lists) {
      set.add(object.toString());
    }
    return set;
  }

  @SuppressWarnings("unchecked")
  public static Map<String, Pair<Class<? extends AbstractTask>, List<AcceptanceStrategy>>> getRegisteredJobs() {
    Map<String, Pair<Class<? extends AbstractTask>, List<AcceptanceStrategy>>> map = new HashMap<>();

    try {
      Set<String> commonStrategyNames = getJobStrategies();
      List<AcceptanceStrategy> commonAcceptStrategies = new ArrayList<>();
      for (String commonStrategyName : commonStrategyNames) {
        AcceptanceStrategy acceptStrategy = ReflectionUtils
            .getInstanceByClassName(commonStrategyName);
        commonAcceptStrategies.add(acceptStrategy);
      }

      List<HierarchicalConfiguration> jobs = config.configurationsAt(".jobs.job");
      for (HierarchicalConfiguration jobConf : jobs) {
        String type = jobConf.getString("[@type]");
        String clazz = jobConf.getString("[@class]");

        List<AcceptanceStrategy> acceptStrategies = new ArrayList<AcceptanceStrategy>(
            commonAcceptStrategies);
        List<HierarchicalConfiguration> strategies = jobConf
            .configurationsAt(".strategies.strategy");
        for (HierarchicalConfiguration strategyConf : strategies) {
          String strategyName = strategyConf.getRootNode().getValue().toString();
          if (!commonStrategyNames.contains(strategyName)) {
            AcceptanceStrategy acceptStrategy = ReflectionUtils
                .getInstanceByClassName(strategyName);
            acceptStrategies.add(acceptStrategy);
          }
        }

        Class<? extends AbstractTask> jobClass = (Class<? extends AbstractTask>) Class
            .forName(clazz);
        Pair<Class<? extends AbstractTask>, List<AcceptanceStrategy>> t2 = new Pair<>(jobClass,
            acceptStrategies);
        map.put(type, t2);
      }
    } catch (Exception e) {
      Throwables.propagate(e);
    }

    return map;
  }
}
