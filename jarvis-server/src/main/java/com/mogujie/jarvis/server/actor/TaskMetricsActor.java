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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import akka.actor.UntypedActor;

import com.mogujie.jarvis.core.domain.ActorEntry;
import com.mogujie.jarvis.core.domain.IdType;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.core.observer.Event;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.dao.TaskMapper;
import com.mogujie.jarvis.dto.Task;
import com.mogujie.jarvis.protocol.ReportProgressProtos.ServerReportProgressResponse;
import com.mogujie.jarvis.protocol.ReportProgressProtos.WorkerReportProgressRequest;
import com.mogujie.jarvis.protocol.ReportStatusProtos.ServerReportStatusResponse;
import com.mogujie.jarvis.protocol.ReportStatusProtos.WorkerReportStatusRequest;
import com.mogujie.jarvis.server.domain.JobKey;
import com.mogujie.jarvis.server.scheduler.controller.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.controller.SchedulerControllerFactory;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.KilledEvent;
import com.mogujie.jarvis.server.scheduler.event.RunningEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.event.UnhandleEvent;

/**
 * Actor used to receive task metrics information (e.g. status, process) 1. send task status to
 * {@link com.mogujie.jarvis.server.actor.JobSchedulerActor } 2. send process to restserver
 *
 * @author guangming
 *
 */
@Named("taskMetricsActor")
@Scope("prototype")
public class TaskMetricsActor extends UntypedActor {

    private JobSchedulerController schedulerController = SchedulerControllerFactory.getController();

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof WorkerReportStatusRequest) {
            WorkerReportStatusRequest msg = (WorkerReportStatusRequest) obj;
            // fullId = jobId_jobVersion_taskId_attemptId
            String fullId = msg.getFullId();
            long jobId = IdUtils.parse(fullId, IdType.JOB_ID);
            long version = IdUtils.parse(fullId, IdType.JOB_VERSION);
            long taskId = IdUtils.parse(fullId, IdType.TASK_ID);
            JobKey jobKey = new JobKey(jobId, version);

            JobStatus status = JobStatus.getInstance(msg.getStatus());
            Event event = new UnhandleEvent();
            if (status.equals(JobStatus.SUCCESS)) {
                event = new SuccessEvent(jobKey, taskId);
            } else if (status.equals(JobStatus.FAILED)) {
                event = new FailedEvent(jobKey, taskId);
            } else if (status.equals(JobStatus.RUNNING)) {
                event = new RunningEvent(jobKey, taskId);
            } else if (status.equals(JobStatus.KILLED)) {
                event = new KilledEvent(jobKey, taskId);
            }
            schedulerController.notify(event);

            ServerReportStatusResponse response = ServerReportStatusResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } else if (obj instanceof WorkerReportProgressRequest) {
            WorkerReportProgressRequest request = (WorkerReportProgressRequest) obj;
            String fullId = request.getFullId();
            long taskId = Long.parseLong(fullId.split("_")[2]);
            float progress = request.getProgress();

            Task task = new Task();
            task.setTaskId(taskId);
            task.setProgress(progress);

            taskMapper.updateByPrimaryKey(task);
            ServerReportProgressResponse response = ServerReportProgressResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } else {
            unhandled(obj);
        }
    }

    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(WorkerReportStatusRequest.class, ServerReportStatusResponse.class, MessageType.SYSTEM));
        list.add(new ActorEntry(WorkerReportProgressRequest.class, ServerReportProgressResponse.class, MessageType.SYSTEM));
        return list;
    }
}
