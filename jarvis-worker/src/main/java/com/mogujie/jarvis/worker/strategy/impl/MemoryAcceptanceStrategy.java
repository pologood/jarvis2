/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午7:57:49
 */

package com.mogujie.jarvis.worker.strategy.impl;

import java.text.DecimalFormat;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.worker.WorkerConfigKeys;
import com.mogujie.jarvis.worker.strategy.AcceptanceResult;
import com.mogujie.jarvis.worker.strategy.AcceptanceStrategy;

/**
 * @author wuya
 *
 */
public class MemoryAcceptanceStrategy implements AcceptanceStrategy {

  private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
  private static final double MAX_MEMORY_USAGE = ConfigUtils.getWorkerConfig()
      .getDouble(WorkerConfigKeys.WORKER_MEMORY_USAGE_THRESHOLD, 0.9);

  @Override
  public AcceptanceResult accept() throws Exception {
    Sigar sigar = new Sigar();
    try {
      Mem mem = sigar.getMem();
      double currentMemoryUsage = mem.getUsedPercent() / 100;
      if (currentMemoryUsage > MAX_MEMORY_USAGE) {
        return new AcceptanceResult(false, "client当前内存使用率"
            + decimalFormat.format(currentMemoryUsage) + ", 超过阈值" + MAX_MEMORY_USAGE);
      }
    } catch (SigarException e) {
      return new AcceptanceResult(false, e.getMessage());
    }

    return new AcceptanceResult(true, "");
  }

}
