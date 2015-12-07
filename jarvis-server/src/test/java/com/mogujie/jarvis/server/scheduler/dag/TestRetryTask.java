/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月2日 上午11:40:13
 */

package com.mogujie.jarvis.server.scheduler.dag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.exeception.JobScheduleException;
import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.server.domain.JobDependencyEntry;
import com.mogujie.jarvis.server.scheduler.event.AddTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.ManualRerunTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.RetryTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.ScheduleEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.plan.ExecutionPlanEntry;
import com.mogujie.jarvis.server.scheduler.plan.PlanGenerator;
import com.mogujie.jarvis.server.scheduler.task.DAGTask;
import com.mogujie.jarvis.server.scheduler.task.TaskGraph;
import com.mogujie.jarvis.server.scheduler.time.TimeScheduler;
import com.mogujie.jarvis.server.scheduler.time.TimeSchedulerFactory;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class TestRetryTask extends TestSchedulerBase {
    private long jobAId = 1;
    private long jobBId = 2;
    private long jobCId = 3;
    private long t1 = new DateTime("2000-10-10").getMillis();
    private long t2 = new DateTime("2000-10-12").getMillis();
    private TaskService taskService = SpringContext.getBean(TaskService.class);
    private JobService jobService = SpringContext.getBean(JobService.class);
    private TaskGraph taskGraph = TaskGraph.INSTANCE;

    @Test
    public void testRetryTask1() {
        AddTaskEvent addTaskEvent = new AddTaskEvent(jobAId, null);
        controller.notify(addTaskEvent);
        Assert.assertEquals(1, taskGraph.getTaskMap().size());
        Assert.assertEquals(1, taskQueue.size());
        List<Long> taskIds = new ArrayList<Long>(taskGraph.getTaskMap().keySet());
        Collections.sort(taskIds);
        long taskAId = taskIds.get(0);
        Task task = taskService.get(taskAId);
        Assert.assertEquals(TaskStatus.READY.getValue(), task.getStatus().intValue());
        Assert.assertEquals(1, task.getAttemptId().intValue());

        SuccessEvent successEvent = new SuccessEvent(jobAId, taskAId);
        controller.notify(successEvent);
        task = taskService.get(taskAId);
        Assert.assertEquals(TaskStatus.SUCCESS.getValue(), task.getStatus().intValue());
        Assert.assertEquals(1, task.getAttemptId().intValue());

        RetryTaskEvent retryEvent = new RetryTaskEvent(0, taskAId);
        controller.notify(retryEvent);
        task = taskService.get(taskAId);
        Assert.assertEquals(TaskStatus.READY.getValue(), task.getStatus().intValue());
        Assert.assertEquals(2, task.getAttemptId().intValue());
    }

    @Test
    public void testManualRerunTask() throws JobScheduleException {
        jobGraph.addJob(jobAId, new DAGJob(jobAId, DAGJobType.TIME), null);
        jobGraph.addJob(jobBId, new DAGJob(jobBId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));
        jobGraph.addJob(jobCId, new DAGJob(jobCId, DAGJobType.DEPEND), Sets.newHashSet(jobAId));

        List<Long> jobIdList = Lists.newArrayList(jobAId);
        List<Long> taskIdList = new ArrayList<Long>();
        Date startDate = new Date(t1);
        Date endDate = new Date(t2);
        boolean runChild = true;
        // 1.生成所有任务的执行计划
        TimeScheduler timeScheduler = TimeSchedulerFactory.getInstance();
        PlanGenerator planGenerator = timeScheduler.getPlanGenerator();
        Range<DateTime> range = Range.closed(new DateTime(startDate), new DateTime(endDate));
        Map<Long, List<ExecutionPlanEntry>> planMap = planGenerator.getReschedulePlan(jobIdList, range);
        // 2.通过新的job依赖关系生成新的task
        for (long jobId : jobIdList) {
            List<ExecutionPlanEntry> planList = planMap.get(jobId);
            for (ExecutionPlanEntry planEntry : planList) {
                // create new task
                long scheduleTime = planEntry.getDateTime().getMillis();
                long taskId = taskService.createTaskByJobId(jobId, scheduleTime);
                planEntry.setTaskId(taskId);
                taskIdList.add(taskId);
            }
        }
        // 3.添加DAGTask到TaskGraph中
        for (long jobId : jobIdList) {
            List<ExecutionPlanEntry> planList = planMap.get(jobId);
            for (ExecutionPlanEntry planEntry : planList) {
                // add to taskGraph
                long taskId = planEntry.getTaskId();
                long scheduleTime = planEntry.getDateTime().getMillis();
                Map<Long, List<Long>> dependTaskIdMap = Maps.newHashMap();
                Map<Long, JobDependencyEntry> dependencyMap = jobService.get(jobId).getDependencies();
                if(dependencyMap != null) {
                    for (long preJobId : dependencyMap.keySet()) {
                        JobDependencyEntry dependencyEntry = dependencyMap.get(preJobId);
                        DependencyExpression dependencyExpression = dependencyEntry.getDependencyExpression();
                        List<Long> dependTaskIds = taskService.getDependTaskIds(jobId, preJobId, scheduleTime, dependencyExpression);
                        dependTaskIdMap.put(preJobId, dependTaskIds);
                    }
                }
                DAGTask dagTask = new DAGTask(jobId, taskId, scheduleTime, dependTaskIdMap);
                taskGraph.addTask(taskId, dagTask);
            }
        }
        // 4.添加依赖关系
        for (long jobId : jobIdList) {
            Set<Long> dependJobIds = jobGraph.getEnableParentJobIds(jobId);
            for (long preJobId: jobIdList) {
                if (dependJobIds.contains(preJobId)) {
                    List<ExecutionPlanEntry> planList = planMap.get(jobId);
                    for (ExecutionPlanEntry planEntry : planList) {
                        long taskId = planEntry.getTaskId();
                        DAGTask dagTask = taskGraph.getTask(taskId);
                        List<Long> dependTaskIds = dagTask.getDependTaskIds();
                        for (Long parentId : dependTaskIds) {
                            taskGraph.addDependency(parentId, taskId);
                        }
                    }
                }
            }
        }
        controller.notify(new ManualRerunTaskEvent(taskIdList));

        // 5.如果需要重跑后续任务，触发后续依赖任务
        if (runChild) {
            List<ExecutionPlanEntry> sortedPlanList = new ArrayList<ExecutionPlanEntry>();
            for (long jobId : planMap.keySet()) {
                List<ExecutionPlanEntry> planList = planMap.get(jobId);
                for (ExecutionPlanEntry planEntry : planList) {
                    long taskId = planEntry.getTaskId();
                    List<DAGTask> children = taskGraph.getChildren(taskId);
                    if (children == null || children.isEmpty()) {
                        sortedPlanList.add(planEntry);
                    }
                }
            }
            Collections.sort(sortedPlanList, new Comparator<ExecutionPlanEntry>(){
                @Override
                public int compare(ExecutionPlanEntry entry1, ExecutionPlanEntry entry2) {
                    return entry1.getDateTime().compareTo(entry2.getDateTime());
                }
            });
            for (ExecutionPlanEntry planEntry : sortedPlanList) {
                long taskId = planEntry.getTaskId();
                DAGTask dagTask = taskGraph.getTask(taskId);
                ScheduleEvent scheduleEvent = new ScheduleEvent(dagTask.getJobId(), taskId, dagTask.getScheduleTime());
                controller.notify(scheduleEvent);
            }
        }
    }
}
