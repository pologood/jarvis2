/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月23日 上午10:16:53
 */

package com.mogujie.jarvis.server.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.domain.ActorEntry;
import com.mogujie.jarvis.core.domain.IdType;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.domain.TaskDetail.TaskDetailBuilder;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.domain.WorkerInfo;
import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.protocol.KillTaskProtos.RestServerKillTaskRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.ServerKillTaskRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.ServerKillTaskResponse;
import com.mogujie.jarvis.protocol.KillTaskProtos.WorkerKillTaskResponse;
import com.mogujie.jarvis.protocol.ManualRerunTaskProtos.RestServerManualRerunTaskRequest;
import com.mogujie.jarvis.protocol.ManualRerunTaskProtos.ServerManualRerunTaskResponse;
import com.mogujie.jarvis.protocol.MapEntryProtos.MapEntry;
import com.mogujie.jarvis.protocol.RemovePlanProtos.RestServerRemovePlanRequest;
import com.mogujie.jarvis.protocol.RemovePlanProtos.ServerRemovePlanResponse;
import com.mogujie.jarvis.protocol.RetryTaskProtos.RestServerRetryTaskRequest;
import com.mogujie.jarvis.protocol.RetryTaskProtos.ServerRetryTaskResponse;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitTaskRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.ServerSubmitTaskResponse;
import com.mogujie.jarvis.server.TaskManager;
import com.mogujie.jarvis.server.TaskQueue;
import com.mogujie.jarvis.server.domain.JobDependencyEntry;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.dag.JobGraph;
import com.mogujie.jarvis.server.scheduler.event.ManualRerunTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.RetryTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.ScheduleEvent;
import com.mogujie.jarvis.server.scheduler.plan.ExecutionPlanEntry;
import com.mogujie.jarvis.server.scheduler.plan.PlanGenerator;
import com.mogujie.jarvis.server.scheduler.task.DAGTask;
import com.mogujie.jarvis.server.scheduler.task.TaskGraph;
import com.mogujie.jarvis.server.scheduler.time.TimeScheduler;
import com.mogujie.jarvis.server.scheduler.time.TimeSchedulerFactory;
import com.mogujie.jarvis.server.service.ConvertValidService;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.FutureUtils;

/**
 * @author guangming
 *
 */
@Named("taskActor")
@Scope("prototype")
public class TaskActor extends UntypedActor {
    @Autowired
    private TaskManager taskManager;
    @Autowired
    private TaskService taskService;
    @Autowired
    private JobService jobService;
    @Autowired
    private ConvertValidService convertValidService;

    private JobGraph jobGraph = JobGraph.INSTANCE;
    private TaskGraph taskGraph = TaskGraph.INSTANCE;
    private TaskQueue taskQueue = TaskQueue.INSTANCE;

    private JobSchedulerController controller = JobSchedulerController.getInstance();

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestServerKillTaskRequest) {
            RestServerKillTaskRequest msg = (RestServerKillTaskRequest) obj;
            killTask(msg);
        } else if (obj instanceof RestServerRetryTaskRequest) {
            RestServerRetryTaskRequest msg = (RestServerRetryTaskRequest) obj;
            retryTask(msg);
        } else if (obj instanceof RestServerManualRerunTaskRequest ) {
            RestServerManualRerunTaskRequest msg = (RestServerManualRerunTaskRequest) obj;
            manualRerunTask(msg);
        } else if (obj instanceof RestServerSubmitTaskRequest) {
            RestServerSubmitTaskRequest msg = (RestServerSubmitTaskRequest) obj;
            submitTask(msg);
        } else if (obj instanceof RestServerRemovePlanRequest) {
            RestServerRemovePlanRequest msg = (RestServerRemovePlanRequest) obj;
            removePlan(msg);
        } else {
            unhandled(obj);
        }
    }

    /**
     * kill Task
     *
     * @param msg
     */
    private void killTask(RestServerKillTaskRequest msg) throws Exception {
        ServerKillTaskResponse response = null;
        long taskId = msg.getTaskId();
        String fullId = "";
        WorkerInfo workerInfo = taskManager.getWorkerInfo(fullId);
        if (workerInfo != null) {
            ActorSelection actorSelection = getContext().actorSelection(workerInfo.getAkkaRootPath() + JarvisConstants.WORKER_AKKA_USER_PATH);
            ServerKillTaskRequest serverRequest = ServerKillTaskRequest.newBuilder().setFullId(fullId).build();
            WorkerKillTaskResponse workerResponse = (WorkerKillTaskResponse) FutureUtils.awaitResult(actorSelection, serverRequest, 30);
            response = ServerKillTaskResponse.newBuilder().setSuccess(workerResponse.getSuccess()).setMessage(workerResponse.getMessage())
                    .build();
        } else {
            response = ServerKillTaskResponse.newBuilder().setSuccess(false).setMessage("Kill task[" + taskId + "] failed").build();
        }
        getSender().tell(response, getSelf());
    }

    /**
     * 按照taskId原地重跑
     *
     * @param msg
     */
    private void retryTask(RestServerRetryTaskRequest msg) {
        controller.notify(new RetryTaskEvent(0, msg.getTaskId()));
        ServerRetryTaskResponse response = ServerRetryTaskResponse.newBuilder().setSuccess(true).build();
        getSender().tell(response, getSelf());
    }

    /**
     * 根据jobId和起止时间，按照新依赖关系重跑
     *
     * @param msg
     */
    private void manualRerunTask(RestServerManualRerunTaskRequest msg) {
        List<Long> jobIdList = msg.getJobIdList();
        List<Long> taskIdList = new ArrayList<Long>();
        Date startDate = new Date(msg.getStartTime());
        Date endDate = new Date(msg.getEndTime());
        boolean runChild = msg.getRunChild();
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

        ServerManualRerunTaskResponse response = ServerManualRerunTaskResponse.newBuilder().setSuccess(true).build();
        getSender().tell(response, getSelf());
    }

    /**
     * 一次性执行任务
     *
     * @param msg
     */
    private void submitTask(RestServerSubmitTaskRequest msg) {
        TaskDetail taskDetail = createRunOnceTask(msg);
        taskQueue.put(taskDetail);
        long taskId = IdUtils.parse(taskDetail.getFullId(), IdType.TASK_ID);
        ServerSubmitTaskResponse response = ServerSubmitTaskResponse.newBuilder().setSuccess(true).setTaskId(taskId).build();
        getSender().tell(response, getSelf());
    }

    /**
     * 删除已有的某一个执行计划
     *
     * @param msg
     */
    private void removePlan(RestServerRemovePlanRequest msg) {
        long taskId = msg.getTaskId();
        long jobId = msg.getJobId();
        DateTime scheduleTime = new DateTime(msg.getScheduleTime());
        TimeScheduler timeScheduler = TimeSchedulerFactory.getInstance();
        taskService.updateStatus(taskId, TaskStatus.REMOVED);
        timeScheduler.removePlan(new ExecutionPlanEntry(jobId, scheduleTime, taskId));
        ServerRemovePlanResponse response = ServerRemovePlanResponse.newBuilder().setSuccess(true).build();
        getSender().tell(response, getSelf());
    }

    private TaskDetail createRunOnceTask(RestServerSubmitTaskRequest request) {
        Task task = convertValidService.convert2Task(request);
        taskService.insert(task);
        long taskId = task.getTaskId();
        TaskDetailBuilder builder = TaskDetail.newTaskDetailBuilder();
        builder.setFullId("0_" + taskId + "_0");
        builder.setAppName(request.getAppAuth().getName());
        builder.setTaskName(request.getTaskName());
        builder.setUser(request.getUser());
        builder.setTaskType(request.getTaskType());
        builder.setContent(request.getContent());
        builder.setGroupId(request.getGroupId());
        builder.setPriority(request.getPriority());
        builder.setRejectRetries(request.getRejectRetries());
        builder.setRejectInterval(request.getRejectInterval());
        builder.setFailedRetries(request.getFailedRetries());
        builder.setFailedInterval(request.getFailedInterval());
        builder.setSchedulingTime(DateTime.now().getMillis() / 1000);
        if (request.getParametersList().size() > 0) {
            Map<String, Object> parameters = Maps.newHashMap();
            for (MapEntry entry : request.getParametersList()) {
                parameters.put(entry.getKey(), entry.getValue());
            }
            builder.setParameters(parameters);
        }

        return builder.build();
    }

    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(RestServerKillTaskRequest.class, ServerKillTaskResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestServerRetryTaskRequest.class, ServerRetryTaskResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestServerSubmitTaskRequest.class, ServerSubmitTaskResponse.class, MessageType.GENERAL));
        return list;
    }
}
