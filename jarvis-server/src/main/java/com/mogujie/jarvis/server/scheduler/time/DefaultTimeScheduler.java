/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月23日 下午3:18:23
 */

package com.mogujie.jarvis.server.scheduler.time;

import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.server.scheduler.event.TimeReadyEvent;
import com.mogujie.jarvis.server.scheduler.plan.ExecutionPlanEntry;
import com.mogujie.jarvis.server.scheduler.plan.PlanGenerator;

/**
 * runtime based TimeScheduler
 *
 * @author guangming
 *
 */
@Repository
public class DefaultTimeScheduler extends TimeScheduler {

    public DefaultTimeScheduler() {
        this.planGenerator = new PlanGenerator();
    }

    @Override
    protected void startPlan(ExecutionPlanEntry entry) {
        long jobId = entry.getJobId();
        DateTime dt = entry.getDateTime();
        controller.notify(new TimeReadyEvent(jobId, dt.getMillis()));
        planGenerator.generateNextPlan(jobId, dt);
    }

    @Override
    public void addJob(long jobId) {
        DateTime scheduleTime = planGenerator.getScheduleTimeAfter(jobId, DateTime.now());
        if (scheduleTime != null) {
            plan.addPlan(jobId, scheduleTime);
        }
    }

    @Override
    public void removeJob(long jobId) {
        plan.removePlan(jobId);
    }

    @Override
    public void modifyJobFlag(long jobId, JobFlag flag) {
        if (flag.equals(JobFlag.DISABLE) || flag.equals(JobFlag.DELETED)) {
            removeJob(jobId);
        } else if (flag.equals(JobFlag.ENABLE)) {
            addJob(jobId);
        }
    }

}
