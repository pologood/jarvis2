/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月17日 下午1:07:46
 */

package com.mogujie.jarvis.jobs.util;

import com.mogujie.jarvis.jobs.domain.HiveJobEntity;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wuya
 *
 */
public class HiveConfigUtils {

  private static XMLConfiguration config;
  private static Map<String, HiveJobEntity> map = new ConcurrentHashMap<String, HiveJobEntity>();
  private static final Logger LOGGER = LogManager.getLogger();

  static {
    try {
      config = new XMLConfiguration("hive-job.xml");
      config.setReloadingStrategy(new FileChangedReloadingStrategy());
    } catch (ConfigurationException e) {
      LOGGER.error("", e);
    }
  }

  public synchronized static HiveJobEntity getHiveJobEntry(String name) {
    map.clear();
    List<HierarchicalConfiguration> list = config.configurationsAt(".App");
    for (HierarchicalConfiguration conf : list) {
      String appName = conf.getString("[@name]");
      String user = conf.getString("[@user]");
      boolean isAdmin = conf.getInt("[@isAdmin]") == 1;
      int maxResultRows = conf.getInt("[@maxResultRows]");
      int maxMapperNum = conf.getInt("[@maxMapperNum]");
      HiveJobEntity entity = new HiveJobEntity(appName, user, isAdmin, maxResultRows, maxMapperNum);
      map.put(appName, entity);
    }

    return map.get(name);
  }

}
