/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月23日 下午3:25:43
 */

package com.mogujie.jarvis.server.scheduler.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.joda.time.DateTime;

import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.server.scheduler.event.RunTaskEvent;
import com.mogujie.jarvis.server.scheduler.plan.AllPlanGenerator;
import com.mogujie.jarvis.server.scheduler.plan.ExecutionPlanEntry;

/**
 * Plan based TimeScheduler
 *
 * @author guangming
 *
 */
public class PlanTimeScheduler extends TimeScheduler {

    private static PlanTimeScheduler instance = new PlanTimeScheduler();

    private PlanTimeScheduler() {
        this.planGenerator = new AllPlanGenerator();

        long period = planGenerator.getPeriod();
        final String startTime = "23:30:00";
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd " + startTime);
        Date firstTime;
        try {
            firstTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sdf.format(new Date()));
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new PlanTimerTask(), firstTime, period);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static PlanTimeScheduler getInstance() {
        return instance;
    }

    class PlanTimerTask extends TimerTask {
        @Override
        public void run() {
            final DateTime startDateTime = DateTime.now().plusDays(1).withTimeAtStartOfDay();
            final DateTime endDateTime = DateTime.now().plusDays(2).withTimeAtStartOfDay();
            planGenerator.generateNextPlan(startDateTime, endDateTime);
        }
    }

    @Override
    protected void startPlan(ExecutionPlanEntry entry) {
        controller.notify(new RunTaskEvent(entry.getJobId(), entry.getTaskId()));
    }

    //当前实现在下一次执行计划生效
    @Override
    public void addJob(long jobId) {
    }

    @Override
    public void removeJob(long jobId) {
    }

    @Override
    public void modifyJobFlag(long jobId, JobFlag flag) {
    }

}
