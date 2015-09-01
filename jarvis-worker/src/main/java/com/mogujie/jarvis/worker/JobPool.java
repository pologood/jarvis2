/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月1日 下午2:53:02
 */

package com.mogujie.jarvis.worker;

import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;

import com.google.common.base.Throwables;
import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.mogujie.jarvis.core.common.util.KryoUtils;
import com.mogujie.jarvis.core.job.AbstractJob;

/**
 * @author wuya
 *
 */
public enum JobPool {

  INSTANCE;

  private static DB db;
  private static final Logger LOGGER = LogManager.getLogger();

  static {
    Options options = new Options();
    options.createIfMissing(true);
    try {
      db = factory.open(new File(ConfigUtils.getWorkerConfig().getString("worker.leveldb.path")),
          options);
    } catch (IOException e) {
      Throwables.propagate(e);
    }
  }

  public static final JobPool getInstance() {
    return INSTANCE;
  }

  public void add(String fullId, AbstractJob job) {
    db.put(bytes(fullId), KryoUtils.writeClassAndObject(job));
  }

  public void remove(String fullId) {
    db.delete(bytes(fullId));
  }

  public AbstractJob get(String fullId) {
    return (AbstractJob) KryoUtils.readClassAndObject(db.get(bytes(fullId)));
  }

  public int size() {
    int size = 0;
    DBIterator iterator = db.iterator();
    try {
      for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
        size++;
      }
    } finally {
      try {
        iterator.close();
      } catch (IOException e) {
        LOGGER.error("", e);
      }
    }
    return size;
  }
}
