/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月23日 下午3:20:27
 */

package com.mogujie.jarvis.server.scheduler.dag.checker;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.core.util.ReflectionUtils;

/**
 * @author guangming
 *
 */
public class DAGDependCheckerFactory {
    public static final String DAG_DEPEND_CHECKER_KEY = "dag.depend.checker";
    public static final String DEFAULT_DAG_DEPEND_CHECKER = DefaultDAGDependChecker.class.getName();

    public static DAGDependChecker create() {
        Configuration conf = ConfigUtils.getServerConfig();
        String className = conf.getString(DAG_DEPEND_CHECKER_KEY, DEFAULT_DAG_DEPEND_CHECKER);
        DAGDependChecker dependStatus = null;
        try {
            dependStatus = ReflectionUtils.getInstanceByClassName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }

        return dependStatus;
    }
}
