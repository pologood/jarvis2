/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:27:40
 */
package com.mogujie.jarvis.server.actor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.domain.IdType;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.observer.Event;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.protocol.ReportTaskProgressProtos.ServerReportTaskProgressResponse;
import com.mogujie.jarvis.protocol.ReportTaskProgressProtos.WorkerReportTaskProgressRequest;
import com.mogujie.jarvis.protocol.ReportTaskStatusProtos.ServerReportTaskStatusResponse;
import com.mogujie.jarvis.protocol.ReportTaskStatusProtos.WorkerReportTaskStatusRequest;
import com.mogujie.jarvis.server.domain.ActorEntry;
import com.mogujie.jarvis.server.domain.JobEntry;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.KilledEvent;
import com.mogujie.jarvis.server.scheduler.event.RunningEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.event.UnhandleEvent;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.service.WorkerService;

import akka.actor.ActorRef;
import akka.actor.Address;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * Actor used to receive task metrics information (e.g. status, process)
 *
 * @author guangming
 *
 */
public class TaskMetricsActor extends UntypedActor {
    private TaskService taskService = Injectors.getInjector().getInstance(TaskService.class);
    private WorkerService workerService = Injectors.getInjector().getInstance(WorkerService.class);
    private JobService jobService = Injectors.getInjector().getInstance(JobService.class);

    private JobSchedulerController schedulerController = JobSchedulerController.getInstance();
    private static final Logger LOGGER = LogManager.getLogger();

    public static Props props() {
        return Props.create(TaskMetricsActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof WorkerReportTaskStatusRequest) {
            WorkerReportTaskStatusRequest msg = (WorkerReportTaskStatusRequest) obj;
            String fullId = msg.getFullId();
            long jobId = IdUtils.parse(fullId, IdType.JOB_ID);
            long taskId = IdUtils.parse(fullId, IdType.TASK_ID);
            TaskStatus status = TaskStatus.parseValue(msg.getStatus());
            LOGGER.info("receive task {} status {}", taskId, status);
            Event event = new UnhandleEvent();
            if (status.equals(TaskStatus.SUCCESS)) {
                event = new SuccessEvent(jobId, taskId);
            } else if (status.equals(TaskStatus.FAILED)) {
                event = new FailedEvent(jobId, taskId);
            } else if (status.equals(TaskStatus.RUNNING)) {
                Address address = getSender().path().address();
                String ip = address.host().get();
                int port = Integer.parseInt(address.port().get().toString());
                int workerId = workerService.getWorkerId(ip, port);
                event = new RunningEvent(jobId, taskId, workerId);
            } else if (status.equals(TaskStatus.KILLED)) {
                event = new KilledEvent(jobId, taskId);
            }
            schedulerController.notify(event);

            ServerReportTaskStatusResponse response = ServerReportTaskStatusResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());

            // http callback
            JobEntry jobEntry = jobService.get(jobId);
            if (jobEntry != null) {
                String params = jobEntry.getJob().getParams();
                Map<String, Object> map = JsonHelper.fromJson2JobParams(params);
                if (map != null && map.containsKey(JarvisConstants.HTTP_CALLBACK_URL)) {
                    String url = map.get(JarvisConstants.HTTP_CALLBACK_URL).toString();
                    Map<String, Object> postFields = Maps.newHashMap();
                    postFields.put("jobId", jobId);
                    postFields.put("taskId", taskId);
                    postFields.put("status", status);
                    postFields.put("url", url);

                    ActorRef httpCallbackActor = getContext().actorOf(HttpCallbackActor.props());
                    httpCallbackActor.tell(postFields, getSelf());
                }
            }
        } else if (obj instanceof WorkerReportTaskProgressRequest) {
            WorkerReportTaskProgressRequest request = (WorkerReportTaskProgressRequest) obj;
            String fullId = request.getFullId();
            long taskId = IdUtils.parse(fullId, IdType.TASK_ID);
            float progress = request.getProgress();
            LOGGER.info("receive task {} progress {}", taskId, progress);
            taskService.updateProgress(taskId, progress);
            ServerReportTaskProgressResponse response = ServerReportTaskProgressResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } else {
            unhandled(obj);
        }
    }

    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(WorkerReportTaskStatusRequest.class, ServerReportTaskStatusResponse.class, MessageType.SYSTEM));
        list.add(new ActorEntry(WorkerReportTaskProgressRequest.class, ServerReportTaskProgressResponse.class, MessageType.SYSTEM));
        return list;
    }
}
