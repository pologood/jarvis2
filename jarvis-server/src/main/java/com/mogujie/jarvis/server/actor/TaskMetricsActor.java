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
import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.core.observer.Event;
import com.mogujie.jarvis.dao.TaskMapper;
import com.mogujie.jarvis.dto.Task;
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

    private JobSchedulerController schedulerController = JobSchedulerController.getInstance();

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof WorkerReportTaskStatusRequest) {
            WorkerReportTaskStatusRequest msg = (WorkerReportTaskStatusRequest) obj;
            String fullId = msg.getFullId();
            String[] idList = fullId.split("_");
            long jobId = Long.parseLong(idList[0]);
            long taskId = Long.parseLong(idList[1]);

            TaskStatus status = TaskStatus.getInstance(msg.getStatus());
            Event event = new UnhandleEvent();
            if (status.equals(TaskStatus.SUCCESS)) {
                event = new SuccessEvent(jobId, taskId);
            } else if (status.equals(TaskStatus.FAILED)) {
                event = new FailedEvent(jobId, taskId);
            } else if (status.equals(TaskStatus.RUNNING)) {
                event = new RunningEvent(jobId, taskId);
            } else if (status.equals(TaskStatus.KILLED)) {
                event = new KilledEvent(jobId, taskId);
            }
            schedulerController.notify(event);

            ServerReportTaskStatusResponse response = ServerReportTaskStatusResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } else if (obj instanceof WorkerReportTaskProgressRequest) {
            WorkerReportTaskProgressRequest request = (WorkerReportTaskProgressRequest) obj;
            String fullId = request.getFullId();
            long taskId = Long.parseLong(fullId.split("_")[1]);
            float progress = request.getProgress();

            Task task = new Task();
            task.setTaskId(taskId);
            task.setProgress(progress);

            taskMapper.updateByPrimaryKey(task);
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
