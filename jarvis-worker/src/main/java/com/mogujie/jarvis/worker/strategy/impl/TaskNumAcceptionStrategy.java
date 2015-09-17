/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午7:57:49
 */

package com.mogujie.jarvis.worker.strategy.impl;

import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.mogujie.jarvis.core.exeception.AcceptionException;
import com.mogujie.jarvis.worker.TaskPool;
import com.mogujie.jarvis.worker.strategy.AcceptionResult;
import com.mogujie.jarvis.worker.strategy.AcceptionStrategy;

/**
 * @author wuya
 *
 */
public class TaskNumAcceptionStrategy implements AcceptionStrategy {

  public static final int JOB_MAX_THRESHOLD = ConfigUtils.getWorkerConfig()
      .getInt("worker.job.num.threshold", 100);

  @Override
  public AcceptionResult accept() throws AcceptionException {
    int currentJobNum = TaskPool.getInstance().size();
    if (currentJobNum > JOB_MAX_THRESHOLD) {
      return new AcceptionResult(false,
          "client当前运行任务数" + currentJobNum + ", 超过阈值" + JOB_MAX_THRESHOLD);
    }

    return new AcceptionResult(true, "");
  }

}
