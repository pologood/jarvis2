/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月23日 下午3:49:12
 */

package com.mogujie.jarvis.server.scheduler.time;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class TimeSchedulerFactory {
    public static final String TIME_SCHEDULER_KEY = "time.scheduler.key";
    public static final String DEFAULT_TIME_SCHEDULER = "default";
    public static final String PLAN_TIME_SCHEDULER = "plan";

    public static TimeScheduler create() {
        Configuration conf = ConfigUtils.getServerConfig();
        String type = conf.getString(TIME_SCHEDULER_KEY, DEFAULT_TIME_SCHEDULER);
        if (type.equalsIgnoreCase(PLAN_TIME_SCHEDULER)) {
            return SpringContext.getBean(PlanTimeScheduler.class);
        } else {
            return SpringContext.getBean(DefaultTimeScheduler.class);
        }
    }
}
