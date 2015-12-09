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

import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import akka.actor.Address;
import akka.actor.UntypedActor;

import com.mogujie.jarvis.core.domain.ActorEntry;
import com.mogujie.jarvis.core.domain.IdType;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.observer.Event;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.protocol.ReportTaskProgressProtos.ServerReportTaskProgressResponse;
import com.mogujie.jarvis.protocol.ReportTaskProgressProtos.WorkerReportTaskProgressRequest;
import com.mogujie.jarvis.protocol.ReportTaskStatusProtos.ServerReportTaskStatusResponse;
import com.mogujie.jarvis.protocol.ReportTaskStatusProtos.WorkerReportTaskStatusRequest;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.KilledEvent;
import com.mogujie.jarvis.server.scheduler.event.RunningEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.event.UnhandleEvent;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.service.WorkerService;

/**
 * Actor used to receive task metrics information (e.g. status, process)
 *
 * @author guangming
 *
 */
@Named("taskMetricsActor")
@Scope("prototype")
public class TaskMetricsActor extends UntypedActor {
    @Autowired
    private TaskService taskService;
    @Autowired
    private WorkerService workerService;

    private JobSchedulerController schedulerController = JobSchedulerController.getInstance();
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof WorkerReportTaskStatusRequest) {
            WorkerReportTaskStatusRequest msg = (WorkerReportTaskStatusRequest) obj;
            String fullId = msg.getFullId();
            long jobId = IdUtils.parse(fullId, IdType.JOB_ID);
            long taskId = IdUtils.parse(fullId, IdType.TASK_ID);
            TaskStatus status = TaskStatus.getInstance(msg.getStatus());
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
