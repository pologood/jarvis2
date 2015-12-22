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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.mybatis.guice.transactional.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.domain.IdType;
import com.mogujie.jarvis.core.domain.JobRelationType;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.domain.TaskDetail.TaskDetailBuilder;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.domain.WorkerInfo;
import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.core.observer.Event;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.protocol.KillTaskProtos.RestServerKillTaskRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.ServerKillTaskRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.ServerKillTaskResponse;
import com.mogujie.jarvis.protocol.KillTaskProtos.WorkerKillTaskResponse;
import com.mogujie.jarvis.protocol.ManualRerunTaskProtos.RestServerManualRerunTaskRequest;
import com.mogujie.jarvis.protocol.ManualRerunTaskProtos.ServerManualRerunTaskResponse;
import com.mogujie.jarvis.protocol.ModifyTaskStatusProtos.RestServerModifyTaskStatusRequest;
import com.mogujie.jarvis.protocol.ModifyTaskStatusProtos.ServerModifyTaskStatusResponse;
import com.mogujie.jarvis.protocol.QueryTaskRelationProtos.RestServerQueryTaskRelationRequest;
import com.mogujie.jarvis.protocol.QueryTaskRelationProtos.ServerQueryTaskRelationResponse;
import com.mogujie.jarvis.protocol.QueryTaskRelationProtos.TaskMapEntry;
import com.mogujie.jarvis.protocol.RetryTaskProtos.RestServerRetryTaskRequest;
import com.mogujie.jarvis.protocol.RetryTaskProtos.ServerRetryTaskResponse;
import com.mogujie.jarvis.protocol.SubmitTaskProtos.RestServerSubmitTaskRequest;
import com.mogujie.jarvis.protocol.SubmitTaskProtos.ServerSubmitTaskResponse;
import com.mogujie.jarvis.server.dispatcher.TaskManager;
import com.mogujie.jarvis.server.dispatcher.TaskQueue;
import com.mogujie.jarvis.server.domain.ActorEntry;
import com.mogujie.jarvis.server.domain.JobDependencyEntry;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.ManualRerunTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.RetryTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.ScheduleEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.event.UnhandleEvent;
import com.mogujie.jarvis.server.scheduler.plan.ExecutionPlan;
import com.mogujie.jarvis.server.scheduler.plan.ExecutionPlanEntry;
import com.mogujie.jarvis.server.scheduler.plan.PlanGenerator;
import com.mogujie.jarvis.server.scheduler.task.DAGTask;
import com.mogujie.jarvis.server.scheduler.task.TaskGraph;
import com.mogujie.jarvis.server.service.ConvertValidService;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.TaskDependService;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.FutureUtils;

import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * @author guangming
 *
 */
public class TaskActor extends UntypedActor {
    private TaskManager taskManager = Injectors.getInjector().getInstance(TaskManager.class);
    private TaskService taskService = Injectors.getInjector().getInstance(TaskService.class);
    private JobService jobService = Injectors.getInjector().getInstance(JobService.class);
    private TaskDependService taskDependService = Injectors.getInjector().getInstance(TaskDependService.class);
    private ConvertValidService convertValidService = Injectors.getInjector().getInstance(ConvertValidService.class);

    private TaskGraph taskGraph = TaskGraph.INSTANCE;
    private TaskQueue taskQueue = Injectors.getInjector().getInstance(TaskQueue.class);
    private JobSchedulerController controller = JobSchedulerController.getInstance();

    private static final Logger LOGGER = LogManager.getLogger();

    public static Props props() {
        return Props.create(TaskActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestServerKillTaskRequest) {
            RestServerKillTaskRequest msg = (RestServerKillTaskRequest) obj;
            killTask(msg);
        } else if (obj instanceof RestServerRetryTaskRequest) {
            RestServerRetryTaskRequest msg = (RestServerRetryTaskRequest) obj;
            retryTask(msg);
        } else if (obj instanceof RestServerManualRerunTaskRequest) {
            RestServerManualRerunTaskRequest msg = (RestServerManualRerunTaskRequest) obj;
            manualRerunTask(msg);
        } else if (obj instanceof RestServerSubmitTaskRequest) {
            RestServerSubmitTaskRequest msg = (RestServerSubmitTaskRequest) obj;
            submitTask(msg);
        } else if (obj instanceof RestServerModifyTaskStatusRequest) {
            RestServerModifyTaskStatusRequest msg = (RestServerModifyTaskStatusRequest) obj;
            modifyTaskStatus(msg);
        } else if (obj instanceof RestServerQueryTaskRelationRequest) {
            RestServerQueryTaskRelationRequest msg = (RestServerQueryTaskRelationRequest) obj;
            queryTaskRelation(msg);
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
        LOGGER.info("start killTask");
        ServerKillTaskResponse response = null;
        String fullId = msg.getFullId();
        long taskId = IdUtils.parse(fullId, IdType.TASK_ID);
        WorkerInfo workerInfo = taskManager.getWorkerInfo(fullId);
        if (workerInfo != null) {
            ActorSelection actorSelection = getContext().actorSelection(workerInfo.getAkkaRootPath() + JarvisConstants.WORKER_AKKA_USER_PATH);
            ServerKillTaskRequest serverRequest = ServerKillTaskRequest.newBuilder().setFullId(fullId).build();
            WorkerKillTaskResponse workerResponse = (WorkerKillTaskResponse) FutureUtils.awaitResult(actorSelection, serverRequest, 30);
            response = ServerKillTaskResponse.newBuilder().setSuccess(workerResponse.getSuccess()).setMessage(workerResponse.getMessage()).build();
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
        LOGGER.info("start retryTask");
        controller.notify(new RetryTaskEvent(msg.getTaskId()));
        ServerRetryTaskResponse response = ServerRetryTaskResponse.newBuilder().setSuccess(true).build();
        getSender().tell(response, getSelf());
    }

    /**
     * 根据jobId和起止时间，按照新依赖关系重跑
     *
     * @param msg
     */
    @Transactional
    private void manualRerunTask(RestServerManualRerunTaskRequest msg) {
        LOGGER.info("start manualRerunTask");
        List<Long> jobIdList = msg.getJobIdList();
        List<Long> taskIdList = new ArrayList<Long>();
        DateTime startDate = new DateTime(msg.getStartTime());
        DateTime endDate = new DateTime(msg.getEndTime());
        boolean runChild = msg.getRunChild();
        // 1.生成所有任务的执行计划
        PlanGenerator planGenerator = new PlanGenerator();
        Range<DateTime> range = Range.closed(startDate, endDate);
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
                if (dependencyMap != null) {
                    for (Entry<Long, JobDependencyEntry> entry : dependencyMap.entrySet()) {
                        long preJobId = entry.getKey();
                        JobDependencyEntry dependencyEntry = entry.getValue();
                        DependencyExpression dependencyExpression = dependencyEntry.getDependencyExpression();
                        List<Long> dependTaskIds = taskService.getDependTaskIds(jobId, preJobId, scheduleTime, dependencyExpression);
                        dependTaskIdMap.put(preJobId, dependTaskIds);
                    }
                }
                if (jobService.get(jobId).getJob().getSerialFlag() > 0) {
                    Task task = taskService.getLastTask(jobId, taskId);
                    if (task != null) {
                        List<Long> dependTaskIds = Lists.newArrayList(task.getTaskId());
                        dependTaskIdMap.put(jobId, dependTaskIds);
                    }
                }
                DAGTask dagTask = new DAGTask(jobId, taskId, scheduleTime, dependTaskIdMap);
                taskGraph.addTask(taskId, dagTask);
            }
        }
        // 4.添加依赖关系
        for (long taskId : taskIdList) {
            DAGTask dagTask = taskGraph.getTask(taskId);
            List<Long> dependTaskIds = dagTask.getDependTaskIds();
            for (long parentId : dependTaskIds) {
                taskGraph.addDependency(parentId, taskId);
            }
        }
        // 5. 重跑任务
        controller.notify(new ManualRerunTaskEvent(taskIdList));

        // 6.如果需要重跑后续任务，触发后续依赖任务
        if (runChild) {
            List<ExecutionPlanEntry> sortedPlanList = new ArrayList<ExecutionPlanEntry>();
            for (Entry<Long, List<ExecutionPlanEntry>> entry : planMap.entrySet()) {
                List<ExecutionPlanEntry> planList = entry.getValue();
                for (ExecutionPlanEntry planEntry : planList) {
                    long taskId = planEntry.getTaskId();
                    List<DAGTask> children = taskGraph.getChildren(taskId);
                    if (children == null || children.isEmpty()) {
                        sortedPlanList.add(planEntry);
                    }
                }
            }
            Collections.sort(sortedPlanList, new Comparator<ExecutionPlanEntry>() {
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
        LOGGER.info("start submitTask");
        ServerSubmitTaskResponse response;
        try {
            TaskDetail taskDetail = createRunOnceTask(msg);
            taskQueue.put(taskDetail);
            long taskId = IdUtils.parse(taskDetail.getFullId(), IdType.TASK_ID);
            response = ServerSubmitTaskResponse.newBuilder().setSuccess(true).setTaskId(taskId).build();
            getSender().tell(response, getSelf());
        } catch (Exception e) {
            response = ServerSubmitTaskResponse.newBuilder().setSuccess(false).setMessage(e.getMessage()).build();
            getSender().tell(response, getSelf());
        }
    }

    /**
     * 强制修改task状态（非管理员禁止使用！！）
     *
     * @param msg
     */
    private void modifyTaskStatus(RestServerModifyTaskStatusRequest msg) {
        LOGGER.info("start modifyTaskStatus");
        long taskId = msg.getTaskId();
        TaskStatus status = TaskStatus.parseValue(msg.getStatus());
        Event event = new UnhandleEvent();
        if (status.equals(TaskStatus.SUCCESS)) {
            event = new SuccessEvent(taskId);
        } else if (status.equals(TaskStatus.FAILED)) {
            event = new FailedEvent(taskId);
        }
        // 1. handle success/failed event
        JobSchedulerController schedulerController = JobSchedulerController.getInstance();
        schedulerController.notify(event);
        // 2. remove from plan if necessary
        ExecutionPlan plan = ExecutionPlan.INSTANCE;
        plan.removePlan(new ExecutionPlanEntry(0, null, taskId));

        ServerModifyTaskStatusResponse response = ServerModifyTaskStatusResponse.newBuilder().setSuccess(true).build();
        getSender().tell(response, getSelf());
    }

    /**
     * 查询task的依赖关系
     *
     * @param msg
     */
    private void queryTaskRelation(RestServerQueryTaskRelationRequest msg) throws Exception {
        LOGGER.info("start queryTaskRelation");
        ServerQueryTaskRelationResponse response;
        try {
            long taskId = msg.getTaskId();
            ServerQueryTaskRelationResponse.Builder builder = ServerQueryTaskRelationResponse.newBuilder();
            Map<Long, List<Long>> taskRelationMap;
            if (msg.getRelationType() == JobRelationType.PARENT.getValue()) {
                taskRelationMap = taskDependService.loadParent(taskId);
            } else {
                DAGTask dagTask = taskGraph.getTask(taskId);
                if (dagTask != null) {
                    List<DAGTask> childDagTasks = taskGraph.getChildren(taskId);
                    taskRelationMap = TaskGraph.convert2TaskMap(childDagTasks);
                } else {
                    taskRelationMap = taskDependService.loadChild(taskId);
                }
            }
            for (Entry<Long, List<Long>> entry : taskRelationMap.entrySet()) {
                long jobId = entry.getKey();
                List<Long> taskList = entry.getValue();
                TaskMapEntry taskMapEntry = TaskMapEntry.newBuilder().setJobId(jobId).addAllTaskId(taskList).build();
                builder.addTaskRelationMap(taskMapEntry);
            }
            response = builder.setSuccess(true).build();
            getSender().tell(response, getSelf());

        } catch (Exception e) {
            response = ServerQueryTaskRelationResponse.newBuilder().setSuccess(false).setMessage(e.getMessage()).build();
            getSender().tell(response, getSelf());
            throw e;
        }
    }

    private TaskDetail createRunOnceTask(RestServerSubmitTaskRequest request) {
        Task task = convertValidService.convert2Task(request);
        long taskId = taskService.insertSelective(task);
        TaskDetailBuilder builder = TaskDetail.newTaskDetailBuilder().setFullId("0_" + taskId + "_0").setAppName(request.getAppAuth().getName())
                .setTaskName(request.getTaskName()).setUser(request.getUser()).setTaskType(request.getTaskType()).setContent(request.getContent())
                .setGroupId(request.getGroupId()).setPriority(request.getPriority()).setRejectRetries(request.getRejectRetries())
                .setRejectInterval(request.getRejectInterval()).setFailedRetries(request.getFailedRetries())
                .setFailedInterval(request.getFailedInterval()).setSchedulingTime(DateTime.now())
                .setParameters(JsonHelper.fromJson2JobParams(request.getParameters()));

        return builder.build();
    }

    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(RestServerKillTaskRequest.class, ServerKillTaskResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestServerRetryTaskRequest.class, ServerRetryTaskResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestServerSubmitTaskRequest.class, ServerSubmitTaskResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestServerManualRerunTaskRequest.class, ServerManualRerunTaskResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestServerModifyTaskStatusRequest.class, ServerModifyTaskStatusResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestServerQueryTaskRelationRequest.class, ServerQueryTaskRelationResponse.class, MessageType.GENERAL));
        return list;
    }
}
