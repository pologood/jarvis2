/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年2月2日 下午3:05:58
 */

package com.mogujie.jarvis.server;

import java.util.TimerTask;

import org.joda.time.DateTime;

import com.google.common.collect.Range;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.service.PlanService;

/**
 * @author guangming
 *
 */
public class PlanTimerTask extends TimerTask {

    private PlanService planSerivce = Injectors.getInjector().getInstance(PlanService.class);

    @Override
    public void run() {
        DateTime now = DateTime.now();
        DateTime startDateTime = now.plusDays(1).withTimeAtStartOfDay();
        DateTime endDateTime = now.plusDays(2).withTimeAtStartOfDay();
        Range<DateTime> range = Range.closedOpen(startDateTime, endDateTime);
        planSerivce.updateJobIds(range);
    }
}
