/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月23日 上午10:16:53
 */

package com.mogujie.jarvis.server.actor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

import com.google.common.collect.Maps;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.domain.ActorEntry;
import com.mogujie.jarvis.core.domain.IdType;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.domain.TaskDetail.TaskDetailBuilder;
import com.mogujie.jarvis.core.domain.WorkerInfo;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.protocol.KillTaskProtos.RestServerKillTaskRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.ServerKillTaskRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.ServerKillTaskResponse;
import com.mogujie.jarvis.protocol.KillTaskProtos.WorkerKillTaskResponse;
import com.mogujie.jarvis.protocol.ManualRetryTaskProtos.RestServerManualRetryTaskRequest;
import com.mogujie.jarvis.protocol.MapEntryProtos.MapEntry;
import com.mogujie.jarvis.protocol.RetryTaskProtos.RestServerRetryTaskRequest;
import com.mogujie.jarvis.protocol.RetryTaskProtos.ServerRetryTaskResponse;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitTaskRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.ServerSubmitTaskResponse;
import com.mogujie.jarvis.server.TaskManager;
import com.mogujie.jarvis.server.TaskQueue;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.event.RetryTaskEvent;
import com.mogujie.jarvis.server.service.IDService;
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
    private IDService idService;

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
        } else if (obj instanceof RestServerManualRetryTaskRequest ) {
            RestServerManualRetryTaskRequest msg = (RestServerManualRetryTaskRequest) obj;
            manualRetryTask(msg);
        } else if (obj instanceof RestServerSubmitTaskRequest) {
            RestServerSubmitTaskRequest msg = (RestServerSubmitTaskRequest) obj;
            submitTask(msg);
        } else {
            unhandled(obj);
        }
    }

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
        List<Long> taskIdList = msg.getTaskIdList();
        boolean runChild = msg.getRunChild();
        controller.notify(new RetryTaskEvent(taskIdList, runChild));
    }

    private void manualRetryTask(RestServerManualRetryTaskRequest msg) {
        List<Long> jobIdList = msg.getJobIdList();
        Date startDate = new Date(msg.getStartTime());
        Date endDate = new Date(msg.getEndTime());
        List<Long> taskIdList = taskService.getTaskIdsByJobIdsBetween(jobIdList, startDate, endDate);
        boolean runChild = msg.getRunChild();
        boolean newDependency = msg.getNewDependency();
        if (!newDependency) {
            //按照历史依赖重跑
            controller.notify(new RetryTaskEvent(taskIdList, runChild));
        } else {
            //TODO 按照新依赖关系重跑
        }
    }

    private void submitTask(RestServerSubmitTaskRequest msg) {
        TaskDetail taskDetail = createRunOnceTask(msg);
        taskQueue.put(taskDetail);
        long taskId = IdUtils.parse(taskDetail.getFullId(), IdType.TASK_ID);
        ServerSubmitTaskResponse response = ServerSubmitTaskResponse.newBuilder().setSuccess(true).setTaskId(taskId).build();
        getSender().tell(response, getSelf());
    }

    private TaskDetail createRunOnceTask(RestServerSubmitTaskRequest request) {
        TaskDetailBuilder builder = TaskDetail.newTaskDetailBuilder();
        builder.setFullId("0_" + idService.getNextTaskId() + "_0");
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
