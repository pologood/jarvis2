/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月23日 下午3:18:23
 */

package com.mogujie.jarvis.server.scheduler.time;

import org.joda.time.DateTime;

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
public class DefaultTimeScheduler extends TimeScheduler {
    private static DefaultTimeScheduler instance = new DefaultTimeScheduler();

    private DefaultTimeScheduler() {
        this.planGenerator = new PlanGenerator();
    }
    public static DefaultTimeScheduler getInstance() {
        return instance;
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
        planGenerator.generateNextPlan(jobId, DateTime.now());
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
