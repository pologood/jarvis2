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
public class AllPlanGenerator extends PlanGenerator {

    private TaskService taskService = SpringContext.getBean(TaskService.class);
    private JobSchedulerController controller = JobSchedulerController.getInstance();

    @Override
    public void generateNextPlan(DateTime startDateTime, DateTime endDateTime) {
        List<ExecutionPlanEntry> nextDayTimeBasedPlans = new ArrayList<ExecutionPlanEntry>();

        // generate next day time based plans
        List<Long> activeTimeBasedJobs = jobGraph.getActiveTimeBasedJobs();
        for (Long jobId : activeTimeBasedJobs) {
            DateTime scheduleTime = getScheduleTimeAfter(jobId, startDateTime);
            while (scheduleTime.isBefore(endDateTime)) {
                ExecutionPlanEntry entry = new ExecutionPlanEntry(jobId, scheduleTime);
                nextDayTimeBasedPlans.add(entry);
                scheduleTime = getScheduleTimeAfter(jobId, scheduleTime);
            }
        }

        // generate next day all tasks
        Collections.sort(nextDayTimeBasedPlans, new Comparator<ExecutionPlanEntry>() {
            public int compare(ExecutionPlanEntry entry1, ExecutionPlanEntry entry2) {
                return entry1.getDateTime().compareTo(entry2.getDateTime());
            }
        });
        for (ExecutionPlanEntry planEntry : nextDayTimeBasedPlans) {
            controller.notify(new TimeReadyEvent(planEntry.getJobId(), planEntry.getDateTime().getMillis()));
        }

        // add time based plan
        for (ExecutionPlanEntry planEntry : nextDayTimeBasedPlans) {
            Task task = taskService.getTaskByJobIdAndScheduleTime(planEntry.getJobId(), planEntry.getDateTime().getMillis());
            if (task != null) {
                planEntry.setTaskId(task.getTaskId());
                plan.addPlan(planEntry);
            }

        }
    }

    @Override
    public long getPeriod() {
        final long time24h = 24 * 60 * 60 * 1000;
        return time24h;
    }

}
