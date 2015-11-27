/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月23日 下午2:02:32
 */

package com.mogujie.jarvis.server.scheduler.plan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.DateTime;

import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.event.TimeReadyEvent;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class NextDayPlanGenerator extends PlanGenerator {

    private TaskService taskService = SpringContext.getBean(TaskService.class);
    private JobSchedulerController controller = JobSchedulerController.getInstance();

    @Override
    public void generateNextPlan() {
        final DateTime startDateTime = DateTime.now().plusDays(1).withTimeAtStartOfDay();
        final DateTime endDateTime = DateTime.now().plusDays(2).withTimeAtStartOfDay();
        List<ExecutionPlanEntry> nextDayPlans = new ArrayList<ExecutionPlanEntry>();

        // generate next day time based plans
        List<Long> activeTimeBasedJobs = jobGraph.getActiveTimeBasedJobs();
        for (Long jobId : activeTimeBasedJobs) {
            DateTime scheduleTime = getScheduleTimeAfter(jobId, startDateTime);
            while (scheduleTime.isBefore(endDateTime)) {
                ExecutionPlanEntry entry = new ExecutionPlanEntry(jobId, scheduleTime);
                nextDayPlans.add(entry);
                scheduleTime = getScheduleTimeAfter(jobId, scheduleTime);
            }
        }

        // generate next day all tasks
        Collections.sort(nextDayPlans, new Comparator<ExecutionPlanEntry>() {
            public int compare(ExecutionPlanEntry entry1, ExecutionPlanEntry entry2) {
                return entry1.getDateTime().compareTo(entry2.getDateTime());
            }
        });
        for (ExecutionPlanEntry planEntry : nextDayPlans) {
            controller.notify(new TimeReadyEvent(planEntry.getJobId(), planEntry.getDateTime().getMillis()));
        }

        List<Task> nextDayTasks = taskService.getTasksBetween(startDateTime.toDate(), endDateTime.toDate());
        if (nextDayTasks != null) {
            for (Task task : nextDayTasks) {
                plan.addPlan(new ExecutionPlanEntry(task.getJobId(), new DateTime(task.getScheduleTime()), task.getTaskId()));
            }
        }
    }

    @Override
    public long getPeriod() {
        final long time24h = 24 * 60 * 60 * 1000;
        return time24h;
    }

}
