/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午2:42:32
 */

package com.mogujie.jarvis.server.scheduler;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.server.scheduler.dag.DAGJobType;

/**
 * @author guangming
 *
 */
public class SchedulerUtil {
    public static final String ENABLE_TEST_MODE = "enable.test.mode";

    public static DAGJobType getDAGJobType(int cycleFlag, int dependFlag, int timeFlag) {
        DAGJobType[] values = DAGJobType.values();
        return values[(cycleFlag << 2) + (dependFlag << 1) + timeFlag];
    }

    public static boolean isTestMode() {
        Configuration conf = ConfigUtils.getServerConfig();
        return conf.getBoolean(ENABLE_TEST_MODE, false);
    }
}
