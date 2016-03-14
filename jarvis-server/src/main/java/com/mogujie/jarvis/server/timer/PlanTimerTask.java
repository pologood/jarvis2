/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年2月2日 下午3:05:58
 */

package com.mogujie.jarvis.server.timer;

import org.joda.time.DateTime;

import com.google.common.collect.Range;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.service.PlanService;

/**
 * 执行计划定时器，每天23:00生成下一天需要跑的job，放入plan表
 *
 * @author guangming
 *
 */
public class PlanTimerTask extends AbstractTimerTask {

    @Override
    public void run() {
        DateTime now = DateTime.now();
        DateTime startDateTime = now.plusDays(1).withTimeAtStartOfDay();
        DateTime endDateTime = now.plusDays(2).withTimeAtStartOfDay();
        Range<DateTime> range = Range.closedOpen(startDateTime, endDateTime);
        PlanService planSerivce = Injectors.getInjector().getInstance(PlanService.class);
        planSerivce.refreshAllPlan(range);
    }

    @Override
    public DateTime getFirstTime(DateTime currentDateTime) {
        final String startTime = "23:30:00";
        String fristDate = currentDateTime.toString("yyyy-MM-dd");
        return new DateTime(fristDate + "T" + startTime);
    }

    @Override
    public long getPeriod() {
        final long time24h = 24 * 60 * 60 * 1000;
        return time24h;
    }
}
