/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午2:42:32
 */

package com.mogujie.jarvis.server.scheduler;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.common.util.ReflectionUtils;
import com.mogujie.jarvis.server.scheduler.DAG.status.IJobDependStatus;
import com.mogujie.jarvis.server.scheduler.DAG.status.MysqlCachedJobDependStatus;

/**
 * @author guangming
 *
 */
public class SchedulerUtil {
    public static String JOB_DEPEND_STATUS_KEY = "job.depend.strategy";
    public static String DEFAULT_JOB_DEPEND_STATUS = MysqlCachedJobDependStatus.class.getName();

    public static IJobDependStatus getJobDependStatus(Configuration conf)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        String className = conf.getString(JOB_DEPEND_STATUS_KEY, DEFAULT_JOB_DEPEND_STATUS);
        return ReflectionUtils.getClassByName(className);
    }
}
