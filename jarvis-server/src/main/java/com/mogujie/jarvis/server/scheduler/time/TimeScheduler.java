/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年10月23日 上午11:32:26
 */

package com.mogujie.jarvis.server.scheduler.time;

import java.util.Iterator;
import java.util.SortedSet;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;
import com.mogujie.jarvis.server.scheduler.plan.ExecutionPlan;
import com.mogujie.jarvis.server.scheduler.plan.ExecutionPlanEntry;
import com.mogujie.jarvis.server.scheduler.plan.PlanGenerator;
import com.mogujie.jarvis.server.service.JobService;

public abstract class TimeScheduler extends Scheduler {

    protected ExecutionPlan plan = ExecutionPlan.INSTANCE;
    private volatile boolean running = true;
    protected JobSchedulerController controller = JobSchedulerController.getInstance();
    protected PlanGenerator planGenerator = new PlanGenerator();

    @Autowired
    protected JobService jobService;

    class TimeScanThread extends Thread {
        public TimeScanThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                if (running) {
                    DateTime now = DateTime.now();
                    SortedSet<ExecutionPlanEntry> planSet = plan.getPlan();
                    Iterator<ExecutionPlanEntry> it = planSet.iterator();
                    while (it.hasNext()) {
                        ExecutionPlanEntry entry = it.next();
                        if (!entry.getDateTime().isAfter(now)) {
                            // 1. start this time based job
                            startPlan(entry);
                            // 2. remove this from plan
                            it.remove();
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
        }
    }
    private TimeScanThread scanThread;

    @Override
    public void handleStartEvent(StartEvent event) {
        if (scanThread == null) {
            scanThread = new TimeScanThread("TimeScanThread");
            scanThread.start();
        }
        running = true;
    }

    @Override
    public void handleStopEvent(StopEvent event) {
        running = false;
    }

    public void removePlan(ExecutionPlanEntry planEntry) {
        plan.removePlan(planEntry);
    }

    public abstract void addJob(long jobId);

    public abstract void removeJob(long jobId);

    public abstract void modifyJobFlag(long jobId, JobStatus flag);

    protected abstract void startPlan(ExecutionPlanEntry entry);

}
