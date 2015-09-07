/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月7日 下午2:31:05
 */

package com.mogujie.jarvis.core;

import com.mogujie.jarvis.protocol.ReportProgressProtos.WorkerReportProgressRequest;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;

public class DefaultProgressReporter implements ProgressReporter {

    private ActorSelection actor;
    private String fullId;

    public DefaultProgressReporter(ActorSelection actor, String fullId) {
        this.actor = actor;
        this.fullId = fullId;
    }

    @Override
    public void report(double progress) {
        WorkerReportProgressRequest request = WorkerReportProgressRequest.newBuilder().setFullId(fullId).setProgress(progress).build();
        actor.tell(request, ActorRef.noSender());
    }

}
