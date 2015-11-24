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

import org.springframework.stereotype.Repository;

import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.server.scheduler.event.RunTaskEvent;
import com.mogujie.jarvis.server.scheduler.plan.ExecutionPlanEntry;
import com.mogujie.jarvis.server.scheduler.plan.NextDayPlanGenerator;

/**
 * Plan based TimeScheduler
 *
 * @author guangming
 *
 */
@Repository
public class PlanTimeScheduler extends TimeScheduler {

    class PlanTimerTask extends TimerTask {
        @Override
        public void run() {
            ((NextDayPlanGenerator)planGenerator).generateNextDayPlan();
        }
    }

    public PlanTimeScheduler() {
        this.planGenerator = new NextDayPlanGenerator();

        //24 hours
        final long time24h = 24 * 60 * 60 * 1000;
        final String startTime = "23:30:00";
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd " + startTime);
        Date firstTime;
        try {
            firstTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sdf.format(new Date()));
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new PlanTimerTask(), firstTime, time24h);
        } catch (ParseException e) {
            throw new RuntimeException(e);
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
