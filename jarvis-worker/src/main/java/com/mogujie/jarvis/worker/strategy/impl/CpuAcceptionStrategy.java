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

import com.mogujie.jarvis.core.exeception.AcceptionException;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.worker.strategy.AcceptionResult;
import com.mogujie.jarvis.worker.strategy.AcceptionStrategy;

/**
 * @author wuya
 *
 */
public class CpuAcceptionStrategy implements AcceptionStrategy {

  private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
  public static final double MAX_CPU_USAGE = ConfigUtils.getWorkerConfig()
      .getDouble("worker.cpu.usage.threshold", 0.85);

  @Override
  public AcceptionResult accept() throws AcceptionException {
    Sigar sigar = new Sigar();
    try {
      CpuPerc perc = sigar.getCpuPerc();
      double currentCpuUsage = perc.getCombined();
      if (currentCpuUsage > MAX_CPU_USAGE) {
        return new AcceptionResult(false,
            "client当前CPU使用率" + decimalFormat.format(currentCpuUsage) + ", 超过阈值" + MAX_CPU_USAGE);
      }
    } catch (SigarException e) {
      return new AcceptionResult(false, e.getMessage());
    }

    return new AcceptionResult(true, "");
  }

}
