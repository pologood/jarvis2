/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年10月9日 上午9:58:47
 */

package com.mogujie.jarvis.worker;

import java.util.Map.Entry;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;

import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.protocol.MapEntryProtos.MapEntry;
import com.mogujie.jarvis.protocol.ReportTaskProtos.WorkerReportTaskRequest;

public class DefaultTaskReporter {

    private ActorSelection actor;

    public DefaultTaskReporter(ActorSelection actor) {
        this.actor = actor;
    }

    public void report(TaskDetail taskDetail) {
        WorkerReportTaskRequest.Builder builder = WorkerReportTaskRequest.newBuilder();
        builder.setFullId(taskDetail.getFullId());
        builder.setTaskName(taskDetail.getTaskName());
        builder.setAppName(taskDetail.getAppName());
        builder.setUser(taskDetail.getUser());
        builder.setJobType(taskDetail.getJobType());
        builder.setContent(taskDetail.getContent());
        builder.setPriority(taskDetail.getPriority());
        int i = 0;
        for (Entry<String, Object> entry : taskDetail.getParameters().entrySet()) {
            MapEntry mapEntry = MapEntry.newBuilder().setKey(entry.getKey()).setValue(entry.getValue().toString()).build();
            builder.addParameters(i++, mapEntry);
        }

        actor.tell(builder.build(), ActorRef.noSender());
    }

}
