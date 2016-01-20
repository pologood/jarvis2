/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2016年1月7日 上午11:00:21
 */

package com.mogujie.jarvis.worker.strategy.impl;

import org.hyperic.sigar.Sigar;
import org.junit.Assert;
import org.junit.Test;

import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.worker.WorkerConfigKeys;
import com.mogujie.jarvis.worker.strategy.AcceptanceStrategy;

public class TestCpuAcceptanceStrategy {

    @Test
    public void testAccept() throws Exception {
        String javaLibraryPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "../classes/native";
        System.setProperty("java.library.path", javaLibraryPath);

        double threshold = ConfigUtils.getWorkerConfig().getDouble(WorkerConfigKeys.WORKER_CPU_USAGE_THRESHOLD, 0.85);
        AcceptanceStrategy acceptanceStrategy = new CpuAcceptanceStrategy();
        Assert.assertEquals(acceptanceStrategy.accept().isAccepted(), new Sigar().getCpuPerc().getCombined() <= threshold);
    }
}
