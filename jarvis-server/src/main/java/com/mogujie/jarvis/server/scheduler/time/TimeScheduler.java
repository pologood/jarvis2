/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年10月23日 上午11:32:26
 */

package com.mogujie.jarvis.server.scheduler.time;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.joda.time.DateTime;

import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.core.expression.ScheduleExpression;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.event.TimeReadyEvent;
import com.mogujie.jarvis.server.scheduler.time.ExecutionPlan.ExecutionPlanEntry;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.util.SpringContext;

public class TimeScheduler extends Thread {

    private ExecutionPlan plan = ExecutionPlan.INSTANCE;
    private volatile boolean running = true;
    private JobSchedulerController controller = JobSchedulerController.getInstance();
    private JobService jobService = SpringContext.getBean(JobService.class);

    @Override
    public void run() {
        while (running) {
            DateTime now = DateTime.now();
            SortedSet<ExecutionPlanEntry> planSet = plan.getPlan();
            Iterator<ExecutionPlanEntry> it = planSet.iterator();
            while (it.hasNext()) {
                ExecutionPlanEntry entry = it.next();
                if (!entry.getDateTime().isAfter(now)) {
                    // 1. start this time based job
                    long jobId = entry.getJobId();
                    long scheduleTime = entry.getDateTime().getMillis();
                    controller.notify(new TimeReadyEvent(jobId, scheduleTime));
                    // 2. remove this from plan
                    it.remove();
                    // 3. add next to plan
                    DateTime nextTime = getScheduleTimeAfter(jobId, entry.getDateTime());
                    plan.addPlan(jobId, nextTime);
                } else {
                    break;
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void shutdown() {
        running = false;
    }

    public void addJob(long jobId) {
        DateTime scheduleTime = getScheduleTimeAfter(jobId, DateTime.now());
        plan.addPlan(jobId, scheduleTime);
    }

    public void removeJob(long jobId) {
        plan.removePlan(jobId);
    }

    public void modifyJobFlag(long jobId, JobFlag flag) {
        if (flag.equals(JobFlag.DISABLE) || flag.equals(JobFlag.DELETED)) {
            removeJob(jobId);
        } else if (flag.equals(JobFlag.ENABLE)) {
            addJob(jobId);
        }
    }

    private DateTime getScheduleTimeAfter(long jobId, DateTime dateTime) {
        DateTime scheduleTime = null;
        List<ScheduleExpression> expressions = jobService.get(jobId).getScheduleExpressions();
        if (expressions != null && expressions.size() > 0) {
            for (ScheduleExpression scheduleExpression : expressions) {
                DateTime nextTime = scheduleExpression.getTimeAfter(dateTime);
                if (scheduleTime == null || scheduleTime.isAfter(nextTime)) {
                    scheduleTime = nextTime;
                }
            }
        }

        return scheduleTime;
    }
}
