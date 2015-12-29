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
/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年10月23日 上午11:32:26
 */

import java.util.Iterator;
import java.util.SortedSet;

import org.joda.time.DateTime;

import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;
import com.mogujie.jarvis.server.scheduler.event.TimeReadyEvent;
import com.mogujie.jarvis.server.service.TaskService;

public class TimeScheduler extends Scheduler {

    private static TimeScheduler instance = new TimeScheduler();
    private TimeScheduler() {}
    public static TimeScheduler getInstance() {
        return instance;
    }

    private ExecutionPlan plan = ExecutionPlan.INSTANCE;
    private volatile boolean running = true;
    private JobSchedulerController controller = JobSchedulerController.getInstance();
    private PlanGenerator planGenerator = new PlanGenerator();
    private TaskService taskService = Injectors.getInjector().getInstance(TaskService.class);

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

    /**
     * add job的时候，首先寻找上一次调度时间，从上一次调度时间计算下一次，这样可以处理异常恢复的时候漏掉需要执行的记录。
     * 如果找不到上一次，说明确实是新加的job，则从当前时间开始计算下一次。
     */
    public void addJob(long jobId) {
        long scheduleTime = DateTime.now().getMillis();
        Task lastone = taskService.getLastTask(jobId, scheduleTime);
        if (lastone != null) {
            scheduleTime = lastone.getScheduleTime().getTime();
        }
        planGenerator.generateNextPlan(jobId, new DateTime(scheduleTime));
    }

    public void removeJob(long jobId) {
        plan.removePlan(jobId);
    }

    public void modifyJobFlag(long jobId, JobStatus flag) {
        if (flag.equals(JobStatus.DISABLE) || flag.equals(JobStatus.DELETED)) {
            removeJob(jobId);
        } else if (flag.equals(JobStatus.ENABLE)) {
            addJob(jobId);
        }
    }

    private void startPlan(ExecutionPlanEntry entry) {
        long jobId = entry.getJobId();
        DateTime dt = entry.getDateTime();
        controller.notify(new TimeReadyEvent(jobId, dt.getMillis()));
        planGenerator.generateNextPlan(jobId, dt);
    }

}

