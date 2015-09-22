/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:27:40
 */
package com.mogujie.jarvis.server.actor;

import javax.inject.Named;

import org.springframework.context.annotation.Scope;

import com.mogujie.jarvis.protocol.ReportProgressProtos.WorkerReportProgressRequest;
import com.mogujie.jarvis.protocol.ReportStatusProtos.WorkerReportStatusRequest;

import akka.actor.ActorSelection;
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

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof WorkerReportStatusRequest) {
            getSchedulerActor().forward(obj, getContext());
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
