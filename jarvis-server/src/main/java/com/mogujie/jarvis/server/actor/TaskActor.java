/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月23日 上午10:16:53
 */

package com.mogujie.jarvis.server.actor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.mybatis.guice.transactional.Transactional;

import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.domain.IdType;
import com.mogujie.jarvis.core.domain.JobRelationType;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.domain.TaskType;
import com.mogujie.jarvis.core.domain.WorkerInfo;
import com.mogujie.jarvis.core.expression.DependencyExpression;
import com.mogujie.jarvis.core.observer.Event;
import com.mogujie.jarvis.core.util.IdUtils;
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
import com.mogujie.jarvis.protocol.RemoveTaskProtos.RestServerRemoveTaskRequest;
import com.mogujie.jarvis.protocol.RemoveTaskProtos.ServerRemoveTaskResponse;
import com.mogujie.jarvis.protocol.RetryTaskProtos.RestServerRetryTaskRequest;
import com.mogujie.jarvis.protocol.RetryTaskProtos.ServerRetryTaskResponse;
import com.mogujie.jarvis.server.dispatcher.PriorityTaskQueue;
import com.mogujie.jarvis.server.dispatcher.TaskManager;
import com.mogujie.jarvis.server.domain.ActorEntry;
import com.mogujie.jarvis.server.domain.JobDependencyEntry;
import com.mogujie.jarvis.server.domain.RetryType;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.TaskRetryScheduler;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.ManualRerunTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.RetryTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.event.UnhandleEvent;
import com.mogujie.jarvis.server.scheduler.task.DAGTask;
import com.mogujie.jarvis.server.scheduler.task.TaskGraph;
import com.mogujie.jarvis.server.scheduler.time.TimePlanEntry;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.TaskDependService;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.FutureUtils;
import com.mogujie.jarvis.server.util.PlanUtil;

/**
 * @author guangming
 */
public class TaskActor extends UntypedActor {
    private TaskManager taskManager = Injectors.getInjector().getInstance(TaskManager.class);
    private TaskService taskService = Injectors.getInjector().getInstance(TaskService.class);
    private JobService jobService = Injectors.getInjector().getInstance(JobService.class);
    private TaskDependService taskDependService = Injectors.getInjector().getInstance(TaskDependService.class);

    private TaskGraph taskGraph = TaskGraph.INSTANCE;
    private JobSchedulerController controller = JobSchedulerController.getInstance();

    private static final Logger LOGGER = LogManager.getLogger();

    public static Props props() {
        return Props.create(TaskActor.class);
    }

    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(RestServerKillTaskRequest.class, ServerKillTaskResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestServerRetryTaskRequest.class, ServerRetryTaskResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestServerManualRerunTaskRequest.class, ServerManualRerunTaskResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestServerModifyTaskStatusRequest.class, ServerModifyTaskStatusResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestServerQueryTaskRelationRequest.class, ServerQueryTaskRelationResponse.class, MessageType.GENERAL));
        list.add(new ActorEntry(RestServerRemoveTaskRequest.class, ServerRemoveTaskResponse.class, MessageType.GENERAL));
        return list;
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
        } else if (obj instanceof RestServerModifyTaskStatusRequest) {
            RestServerModifyTaskStatusRequest msg = (RestServerModifyTaskStatusRequest) obj;
            modifyTaskStatus(msg);
        } else if (obj instanceof RestServerQueryTaskRelationRequest) {
            RestServerQueryTaskRelationRequest msg = (RestServerQueryTaskRelationRequest) obj;
            queryTaskRelation(msg);
        } else if (obj instanceof RestServerRemoveTaskRequest) {
            RestServerRemoveTaskRequest msg = (RestServerRemoveTaskRequest) obj;
            removeTask(msg);
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
        ServerRetryTaskResponse response;
        try {
            long taskId = msg.getTaskId();
            LOGGER.info("start retryTask taskId:{}", taskId);

            Task task = taskService.get(taskId);
            TaskStatus oldStatus = TaskStatus.parseValue(task.getStatus());
            if (!oldStatus.equals(TaskStatus.FAILED) && !oldStatus.equals(TaskStatus.KILLED)) {
                throw new IllegalArgumentException("Only status FAILED|KILLED could be retried.");
            }
            controller.notify(new RetryTaskEvent(taskId));
            response = ServerRetryTaskResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } catch (Exception ex) {
            response = ServerRetryTaskResponse.newBuilder().setSuccess(false).setMessage(ex.getMessage()).build();
            getSender().tell(response, getSelf());
            LOGGER.error("retryTask error", ex);
            throw ex;
        }
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
        // 1.生成所有任务的执行计划
        Range<DateTime> range = Range.closed(startDate, endDate);
        Map<Long, List<TimePlanEntry>> planMap = PlanUtil.getReschedulePlan(jobIdList, range);
        // 2.生成新的task
        long scheduleTime = DateTime.now().getMillis();
        for (long jobId : jobIdList) {
            List<TimePlanEntry> planList = planMap.get(jobId);
            for (TimePlanEntry planEntry : planList) {
                // create new task
                long dataTime = planEntry.getDateTime().getMillis();
                long taskId = taskService.createTaskByJobId(jobId, scheduleTime, dataTime, TaskType.RERUN);
                planEntry.setTaskId(taskId);
                taskIdList.add(taskId);
            }
        }
        // 3.确定task依赖关系，添加DAGTask到TaskGraph中
        for (long jobId : jobIdList) {
            List<TimePlanEntry> planList = planMap.get(jobId);
            for (int i = 0; i < planList.size(); i++) {
                TimePlanEntry planEntry = planList.get(i);
                long taskId = planEntry.getTaskId();
                long dataTime = planEntry.getDateTime().getMillis();
                Map<Long, List<Long>> dependTaskIdMap = Maps.newHashMap();
                Map<Long, JobDependencyEntry> dependencyMap = jobService.get(jobId).getDependencies();
                if (dependencyMap != null) {
                    for (Entry<Long, JobDependencyEntry> entry : dependencyMap.entrySet()) {
                        long preJobId = entry.getKey();
                        if (jobIdList.contains(preJobId)) {
                            JobDependencyEntry dependencyEntry = entry.getValue();
                            DependencyExpression dependencyExpression = dependencyEntry.getDependencyExpression();
                            List<Long> dependTaskIds = getDependTaskIds(planMap.get(preJobId), dataTime, dependencyExpression);
                            dependTaskIdMap.put(preJobId, dependTaskIds);
                        }
                    }
                }
                //如果是串行任务
                if (jobService.get(jobId).getJob().getIsSerial()) {
                    if (i > 0) {
                        // 增加自依赖
                        long preTaskId = planList.get(i - 1).getTaskId();
                        List<Long> dependTaskIds = Lists.newArrayList(preTaskId);
                        dependTaskIdMap.put(jobId, dependTaskIds);
                    }
                }
                // add to taskGraph
                DAGTask dagTask = new DAGTask(jobId, taskId, scheduleTime, dataTime, dependTaskIdMap);
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

        ServerManualRerunTaskResponse response = ServerManualRerunTaskResponse.newBuilder().setSuccess(true).build();
        getSender().tell(response, getSelf());
    }

    private List<Long> getDependTaskIds(List<TimePlanEntry> planList, long dataTime, DependencyExpression dependencyExpression) {
        List<Long> dependTaskIds = new ArrayList<Long>();
        if (dependencyExpression == null) {
            for (TimePlanEntry entry : planList) {
                if (entry.getDateTime().getMillis() == dataTime) {
                    dependTaskIds.add(entry.getTaskId());
                    break;
                }
            }
        } else {
            Range<DateTime> range = dependencyExpression.getRange(new DateTime(dataTime));
            for (TimePlanEntry entry : planList) {
                if (range.contains(entry.getDateTime())) {
                    dependTaskIds.add(entry.getTaskId());
                }
            }
        }
        return dependTaskIds;
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
        String reason = "Manual modify task status.";
        if (status.equals(TaskStatus.SUCCESS)) {
            Task task = taskService.get(taskId);
            event = new SuccessEvent(task.getJobId(), taskId, task.getScheduleTime().getTime(), TaskType.parseValue(task.getType()), reason);
        } else if (status.equals(TaskStatus.FAILED)) {
            event = new FailedEvent(taskId, reason);
        }
        // handle success/failed event
        JobSchedulerController schedulerController = JobSchedulerController.getInstance();
        schedulerController.notify(event);

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

    /**
     * 删除task
     *
     * @param msg
     */
    private void removeTask(RestServerRemoveTaskRequest msg) throws Exception {
        LOGGER.info("start removeTask");
        ServerRemoveTaskResponse response;
        try {
            long taskId = msg.getTaskId();
            // 1. update taskService
            taskService.updateStatus(taskId, TaskStatus.REMOVED);
            // 2. remove from taskGraph
            taskGraph.removeTask(taskId);
            // 3. remove from TaskQueue
            PriorityTaskQueue taskQueue = Injectors.getInjector().getInstance(PriorityTaskQueue.class);
            Task task = taskService.get(taskId);
            String fullId = IdUtils.getFullId(task.getJobId(), taskId, task.getAttemptId());
            taskQueue.removeByKey(fullId);
            // 4. remove from RetryScheduler
            String jobIdWithTaskId = fullId.replaceAll("_\\d+$", "");
            TaskRetryScheduler retryScheduler = TaskRetryScheduler.INSTANCE;
            retryScheduler.remove(jobIdWithTaskId, RetryType.FAILED_RETRY);
            retryScheduler.remove(jobIdWithTaskId, RetryType.REJECT_RETRY);
        } catch (Exception e) {
            response = ServerRemoveTaskResponse.newBuilder().setSuccess(false).setMessage(e.getMessage()).build();
            getSender().tell(response, getSelf());
            throw e;
        }
    }

}
