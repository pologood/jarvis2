/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午7:57:49
 */

package com.mogujie.jarvis.worker.strategy.impl;

import java.text.DecimalFormat;

import org.hyperic.sigar.CpuPerc;
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
public class CpuAcceptanceStrategy implements AcceptanceStrategy {

  private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
  public static final double MAX_CPU_USAGE = ConfigUtils.getWorkerConfig()
      .getDouble(WorkerConfigKeys.WORKER_CPU_USAGE_THRESHOLD, 0.85);

  @Override
  public AcceptanceResult accept() throws Exception {
    Sigar sigar = new Sigar();
    try {
      CpuPerc perc = sigar.getCpuPerc();
      double currentCpuUsage = perc.getCombined();
      if (currentCpuUsage > MAX_CPU_USAGE) {
        return new AcceptanceResult(false,
            "client当前CPU使用率" + decimalFormat.format(currentCpuUsage) + ", 超过阈值" + MAX_CPU_USAGE);
      }
    } catch (SigarException e) {
      return new AcceptanceResult(false, e.getMessage());
    }

    return new AcceptanceResult(true, "");
  }

}
