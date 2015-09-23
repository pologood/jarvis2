/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:27:40
 */
package com.mogujie.jarvis.server.actor;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.protocol.ReportProgressProtos.WorkerReportProgressRequest;
import com.mogujie.jarvis.protocol.ReportStatusProtos.WorkerReportStatusRequest;
import com.mogujie.jarvis.server.JobSchedulerController;
import com.mogujie.jarvis.server.observer.Event;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.KilledEvent;
import com.mogujie.jarvis.server.scheduler.event.RunningEvent;
import com.mogujie.jarvis.server.scheduler.event.SuccessEvent;
import com.mogujie.jarvis.server.scheduler.event.UnhandleEvent;

/**
 * Actor used to receive task metrics information (e.g. status, process)
 * 1. send task status to {@link com.mogujie.jarvis.server.actor.JobSchedulerActor }
 * 2. send process to restserver
 *
 * @author guangming
 *
 */
@Named("taskMetricsActor")
@Scope("prototype")
public class TaskMetricsActor extends UntypedActor {
    @Autowired
    JobSchedulerController schedulerController;

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
            // TODO
        } else {
            unhandled(obj);
        }
    }

    private ActorSelection getSchedulerActor() {
        return getContext().actorSelection("/path/to/JobSchedulerActor");
    }

}
