/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月7日 下午5:03:29
 */

package com.mogujie.jarvis.server.scheduler.plan;

import org.joda.time.DateTime;

import com.google.common.collect.Range;

/**
 * @author guangming
 *
 */
public interface PlanPeriod {

    public String getStartTime();

    public Range<DateTime> getPlanRange();

    public long getPeriod();
}
