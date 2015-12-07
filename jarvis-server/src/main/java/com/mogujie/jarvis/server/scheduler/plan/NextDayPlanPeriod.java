/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月7日 下午5:07:35
 */

package com.mogujie.jarvis.server.scheduler.plan;

import org.joda.time.DateTime;

import com.google.common.collect.Range;

/**
 * @author guangming
 *
 */
public class NextDayPlanPeriod implements PlanPeriod {

    public String getStartTime() {
        final String startTime = "23:30:00";
        return startTime;
    }

    public Range<DateTime> getPlanRange() {
        final DateTime startDateTime = DateTime.now().plusDays(1).withTimeAtStartOfDay();
        final DateTime endDateTime = DateTime.now().plusDays(2).withTimeAtStartOfDay();
        return Range.closedOpen(startDateTime, endDateTime);
    }

    public long getPeriod() {
        final long time24h = 24 * 60 * 60 * 1000;
        return time24h;
    }
}
