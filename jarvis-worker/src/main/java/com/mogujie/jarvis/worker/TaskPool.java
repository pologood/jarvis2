/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月1日 下午2:53:02
 */

package com.mogujie.jarvis.worker;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @author wuya
 *
 */
public enum TaskPool {

  INSTANCE;

  private Map<String, AbstractTask> pool = Maps.newConcurrentMap();

  public void add(String fullId, AbstractTask task) {
    pool.put(fullId, task);
  }

  public void remove(String fullId) {
    pool.remove(fullId);
  }

  public AbstractTask get(String fullId) {
    return pool.get(fullId);
  }

  public int size() {
    return pool.size();
  }
}