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

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

import com.mogujie.jarvis.protocol.ReportProgressProtos.WorkerReportProgressRequest;
import com.mogujie.jarvis.protocol.ReportStatusProtos.WorkerReportStatusRequest;

/**
 * Actor used to receive job metrics information (e.g. status, process) 1. send job status to
 * {@link com.mogujie.jarvis.server.actor.JobSchedulerActor } 2. send process to restserver
 *
 * @author guangming
 *
 */
@Named("JobMetricsActor")
@Scope("prototype")
public class JobMetricsActor extends UntypedActor {

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
