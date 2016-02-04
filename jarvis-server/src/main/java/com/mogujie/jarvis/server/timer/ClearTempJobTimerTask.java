/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年2月4日 上午10:57:36
 */

package com.mogujie.jarvis.server.timer;

import org.joda.time.DateTime;

import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.service.JobService;

/**
 * 临时任务清理定时器，每周一凌晨3点清理之前所有临时任务
 *
 * @author guangming
 *
 */
public class ClearTempJobTimerTask extends AbstractTimerTask {

    @Override
    public void run() {
        DateTime now = DateTime.now();
        JobService jobService = Injectors.getInjector().getInstance(JobService.class);
        jobService.clearTempJobsBefore(now);
    }

    @Override
    public DateTime getFirstTime(DateTime currentDateTime) {
        final String startTime = "03:00:00";
        String fristDate = currentDateTime.withDayOfWeek(1).toString("yyyy-MM-dd");
        return new DateTime(fristDate + "T" + startTime);
    }

    @Override
    public long getPeriod() {
        final long oneWeek = 7 * 24 * 60 * 60 * 1000;
        return oneWeek;
    }
}
