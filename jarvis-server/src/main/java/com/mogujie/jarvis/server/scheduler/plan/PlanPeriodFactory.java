/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月7日 下午5:20:19
 */

package com.mogujie.jarvis.server.scheduler.plan;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.core.util.ReflectionUtils;


/**
 * @author guangming
 *
 */
public class PlanPeriodFactory {
    public static final String PLAN_PERIOD_KEY = "plan.period.key";
    public static final String DEFAULT_PLAN_PERIOD = NextDayPlanPeriod.class.getName();

    public static PlanPeriod create() {
        Configuration conf = ConfigUtils.getServerConfig();
        String className = conf.getString(PLAN_PERIOD_KEY, DEFAULT_PLAN_PERIOD);
        PlanPeriod planPeriod;
        try {
            planPeriod = ReflectionUtils.getInstanceByClassName(className);
        } catch (ClassNotFoundException e) {
            planPeriod = new NextDayPlanPeriod();
        }
        return planPeriod;
    }

}
