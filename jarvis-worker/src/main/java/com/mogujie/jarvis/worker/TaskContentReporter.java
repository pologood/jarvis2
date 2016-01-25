/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年10月9日 上午9:58:47
 */

package com.mogujie.jarvis.worker;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;

import com.mogujie.jarvis.core.TaskReporter;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.protocol.ReportTaskProtos.WorkerReportTaskContentRequest;

public class TaskContentReporter implements TaskReporter {

    private ActorSelection actor;
    private ActorRef sender;

    public TaskContentReporter(ActorSelection actor, ActorRef sender) {
        this.actor = actor;
        this.sender = sender;
    }

    @Override
    public void report(TaskDetail taskDetail) {
        WorkerReportTaskContentRequest request = WorkerReportTaskContentRequest.newBuilder()
                .setFullId(taskDetail.getFullId())
                .setContent(taskDetail.getContent())
                .build();

        actor.tell(request, sender);
    }

}
