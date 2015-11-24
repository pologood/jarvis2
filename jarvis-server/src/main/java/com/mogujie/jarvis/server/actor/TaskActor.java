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

import javax.inject.Named;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

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
import com.mogujie.jarvis.server.util.FutureUtils;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

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
    private IDService idService;

    private TaskQueue taskQueue = TaskQueue.INSTANCE;

    private JobSchedulerController controller = JobSchedulerController.getInstance();

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestServerKillTaskRequest) {
            ServerKillTaskResponse serverResponse = null;
            RestServerKillTaskRequest restServerRequest = (RestServerKillTaskRequest) obj;
            long taskId = restServerRequest.getTaskId();
            String fullId = "";
            WorkerInfo workerInfo = taskManager.getWorkerInfo(fullId);
            if (workerInfo != null) {
                ActorSelection actorSelection = getContext().actorSelection(workerInfo.getAkkaRootPath() + JarvisConstants.WORKER_AKKA_USER_PATH);
                ServerKillTaskRequest serverRequest = ServerKillTaskRequest.newBuilder().setFullId(fullId).build();
                WorkerKillTaskResponse workerResponse = (WorkerKillTaskResponse) FutureUtils.awaitResult(actorSelection, serverRequest, 30);
                serverResponse = ServerKillTaskResponse.newBuilder().setSuccess(workerResponse.getSuccess()).setMessage(workerResponse.getMessage())
                        .build();
            } else {
                serverResponse = ServerKillTaskResponse.newBuilder().setSuccess(false).setMessage("Kill task[" + taskId + "] failed").build();
            }
            getSender().tell(serverResponse, getSelf());
        } else if (obj instanceof RestServerRetryTaskRequest) {
            RestServerRetryTaskRequest msg = (RestServerRetryTaskRequest) obj;
            long taskId = msg.getTaskId();
            boolean runChild = msg.getRunChild();
            controller.notify(new RetryTaskEvent(0, taskId, runChild));
        } else if (obj instanceof RestServerSubmitTaskRequest) {
            RestServerSubmitTaskRequest request = (RestServerSubmitTaskRequest) obj;
            TaskDetail taskDetail = createRunOnceTask(request);
            taskQueue.put(taskDetail);
            long taskId = IdUtils.parse(taskDetail.getFullId(), IdType.TASK_ID);
            ServerSubmitTaskResponse response = ServerSubmitTaskResponse.newBuilder().setSuccess(true).setTaskId(taskId).build();
            getSender().tell(response, getSelf());
        } else {
            unhandled(obj);
        }
    }

    public TaskDetail createRunOnceTask(RestServerSubmitTaskRequest request) {
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
