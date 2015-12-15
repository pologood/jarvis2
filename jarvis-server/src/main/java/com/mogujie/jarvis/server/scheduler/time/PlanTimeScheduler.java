/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月23日 下午3:25:43
 */

package com.mogujie.jarvis.server.scheduler.time;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.joda.time.DateTime;

import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.server.scheduler.event.RunTaskEvent;
import com.mogujie.jarvis.server.scheduler.plan.ExecutionPlanEntry;
import com.mogujie.jarvis.server.scheduler.plan.PlanPeriod;
import com.mogujie.jarvis.server.scheduler.plan.PlanPeriodFactory;

/**
 * Plan based TimeScheduler
 *
 * @author guangming
 *
 */
public class PlanTimeScheduler extends TimeScheduler {

    private static PlanTimeScheduler instance = new PlanTimeScheduler();
    private PlanPeriod planPeriod = PlanPeriodFactory.create();

    private PlanTimeScheduler() {
        final long period = planPeriod.getPeriod();
        final String startTime = planPeriod.getStartTime();
        try {
            String currentDate = DateTime.now().toString("yyyy-MM-dd");
            Date firstTime = (new DateTime(currentDate + "T" + startTime)).toDate();
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new PlanTimerTask(), firstTime, period);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PlanTimeScheduler getInstance() {
        return instance;
    }

    class PlanTimerTask extends TimerTask {
        @Override
        public void run() {
            planGenerator.generateAllPlan(planPeriod.getPlanRange());
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
