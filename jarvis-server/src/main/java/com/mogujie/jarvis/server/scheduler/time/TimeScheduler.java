/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月29日 下午4:17:40
 */

package com.mogujie.jarvis.server.scheduler.time;

/**
 * @author guangming
 *
 */
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.joda.time.DateTime;

import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.dag.DAGJob;
import com.mogujie.jarvis.server.scheduler.dag.DAGJobType;
import com.mogujie.jarvis.server.scheduler.dag.JobGraph;
import com.mogujie.jarvis.server.scheduler.event.AddPlanEvent;
import com.mogujie.jarvis.server.scheduler.event.AddTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;
import com.mogujie.jarvis.server.util.PlanUtil;

public class TimeScheduler extends Scheduler {

    private static TimeScheduler instance = new TimeScheduler();
    private TimeScheduler() {}
    public static TimeScheduler getInstance() {
        return instance;
    }

    private TimePlan plan = TimePlan.INSTANCE;
    private JobGraph jobGraph = JobGraph.INSTANCE;
    private volatile boolean running = true;
    private JobSchedulerController controller = JobSchedulerController.getInstance();

    class TimeScanThread extends Thread {
        public TimeScanThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                if (running) {
                    DateTime now = DateTime.now();
                    Queue<TimePlanEntry> planQueue = plan.getPlan();
                    while (!planQueue.isEmpty()) {
                        TimePlanEntry entry = planQueue.peek();
                        if (!entry.getDateTime().isAfter(now)) {
                            // 1. start this time based job
                            submitJob(entry);
                            // 2. remove this from plan
                            planQueue.poll();
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

    @Subscribe
    public void handleAddPlanEvent(AddPlanEvent event) {
        long jobId = event.getJobId();
        long scheduleTime = event.getScheduleTime();
        Map<Long, List<Long>> dependTaskIdMap = event.getDependTaskIdMap();
        TimePlanEntry entry = new TimePlanEntry(jobId, new DateTime(scheduleTime), dependTaskIdMap);
        plan.addPlan(entry);
    }

    private void submitJob(TimePlanEntry entry) {
        long jobId = entry.getJobId();
        DateTime dt = entry.getDateTime();
        Map<Long, List<Long>> dependTaskIdMap = entry.getDependTaskIdMap();
        AddTaskEvent event = new AddTaskEvent(jobId, dt.getMillis(), dependTaskIdMap);
        controller.notify(event);
        DAGJob dagJob = jobGraph.getDAGJob(jobId);
        // 如果是纯时间任务，自动计算下一次
        if (dagJob.getType().equals(DAGJobType.TIME)) {
            DateTime nextTime = PlanUtil.getScheduleTimeAfter(jobId, dt);
            plan.addPlan(jobId, nextTime);
        }
    }
}

