/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午7:57:49
 */

package com.mogujie.jarvis.worker.strategy.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.DecimalFormat;

import com.mogujie.jarvis.core.exeception.AcceptionException;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.worker.strategy.AcceptionResult;
import com.mogujie.jarvis.worker.strategy.AcceptionStrategy;

/**
 * @author wuya
 *
 */
public class LoadAcceptionStrategy implements AcceptionStrategy {

  private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
  public static final int CPU_NUM = Runtime.getRuntime().availableProcessors();
  public static final double LOAD_THRESHOLD = ConfigUtils.getWorkerConfig()
      .getDouble("worker.cpu.load.avg.threshold", CPU_NUM * 1.5);

  @Override
  public AcceptionResult accept() throws AcceptionException {
    OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory
        .getOperatingSystemMXBean();
    double currentLoad = bean.getSystemLoadAverage();
    if (currentLoad > LOAD_THRESHOLD) {
      return new AcceptionResult(false,
          "client当前CPU Load " + decimalFormat.format(currentLoad) + ", 超过阈值" + LOAD_THRESHOLD);
    }

    return new AcceptionResult(true, "");
  }

}
