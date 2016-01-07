/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2016年1月7日 上午11:20:56
 */

package com.mogujie.jarvis.worker.strategy.impl;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.junit.Assert;
import org.junit.Test;

import com.mogujie.jarvis.core.exeception.AcceptanceException;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.worker.WorkerConfigKeys;
import com.mogujie.jarvis.worker.strategy.AcceptanceStrategy;

public class TestMemoryAcceptanceStrategy {

    @Test
    public void testAccept() throws AcceptanceException, SigarException {
        String javaLibraryPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "../classes/native";
        System.setProperty("java.library.path", javaLibraryPath);

        double threshold = ConfigUtils.getWorkerConfig().getDouble(WorkerConfigKeys.WORKER_MEMORY_USAGE_THRESHOLD, 0.9);
        AcceptanceStrategy acceptanceStrategy = new MemoryAcceptanceStrategy();
        Assert.assertEquals(acceptanceStrategy.accept().isAccepted(), new Sigar().getMem().getUsedPercent() / 100 <= threshold);
    }
}