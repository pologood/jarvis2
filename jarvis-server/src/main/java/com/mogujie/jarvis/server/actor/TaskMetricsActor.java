/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:27:40
 */
package com.mogujie.jarvis.server.actor;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.observer.Event;
import com.mogujie.jarvis.dao.TaskMapper;
import com.mogujie.jarvis.dto.Task;
import com.mogujie.jarvis.protocol.ReportProgressProtos.WorkerReportProgressRequest;
import com.mogujie.jarvis.protocol.ReportStatusProtos.WorkerReportStatusRequest;
import com.mogujie.jarvis.server.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.KilledEvent;
import com.mogujie.jarvis.server.scheduler.event.RunningEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.event.UnhandleEvent;

import akka.actor.UntypedActor;

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
    @Autowired
    private JobSchedulerController schedulerController;

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof WorkerReportStatusRequest) {
            WorkerReportStatusRequest msg = (WorkerReportStatusRequest) obj;
            String fullId = msg.getFullId();
            String[] idList = fullId.split("_");
            long jobId = Long.valueOf(idList[0]);
            long taskId = Long.valueOf(idList[1]);

            JobStatus status = JobStatus.getInstance(msg.getStatus());
            Event event = new UnhandleEvent();
            if (status.equals(JobStatus.SUCCESS)) {
                event = new SuccessEvent(jobId, taskId);
            } else if (status.equals(JobStatus.FAILED)) {
                event = new FailedEvent(jobId, taskId);
            } else if (status.equals(JobStatus.RUNNING)) {
                event = new RunningEvent(jobId, taskId);
            } else if (status.equals(JobStatus.KILLED)) {
                event = new KilledEvent(jobId, taskId);
            }
            schedulerController.notify(event);
        } else if (obj instanceof WorkerReportProgressRequest) {
            WorkerReportProgressRequest request = (WorkerReportProgressRequest) obj;
            String fullId = request.getFullId();
            long taskId = Long.parseLong(fullId.split("_")[1]);
            float progress = request.getProgress();

            Task task = new Task();
            task.setTaskId(taskId);
            task.setProgress(progress);

            taskMapper.updateByPrimaryKey(task);
        } else {
            unhandled(obj);
        }
    }

    public static Set<Class<?>> handledMessages() {
        Set<Class<?>> set = new HashSet<>();
        set.add(WorkerReportStatusRequest.class);
        set.add(WorkerReportProgressRequest.class);
        return set;
    }
}
