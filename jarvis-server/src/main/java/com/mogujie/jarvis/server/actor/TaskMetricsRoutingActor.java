/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya Create Date: 2015年12月5日 下午5:51:05
 */

package com.mogujie.jarvis.server.actor;

import java.util.ArrayList;
import java.util.List;

import com.mogujie.jarvis.protocol.ReportTaskProgressProtos.WorkerReportTaskProgressRequest;
import com.mogujie.jarvis.protocol.ReportTaskStatusProtos.WorkerReportTaskStatusRequest;
import com.mogujie.jarvis.server.domain.ActorEntry;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class TaskMetricsRoutingActor extends UntypedActor {

    private int size;
    private int index = 0;
    private List<ActorRef> actors = new ArrayList<ActorRef>();

    public TaskMetricsRoutingActor(int size) {
        this.size = size;
        for (int i = 0; i < size; i++) {
            ActorRef taskActor = getContext().actorOf(TaskMetricsActor.props());
            actors.add(taskActor);
        }
    }

    public static Props props(int size) {
        return Props.create(TaskMetricsRoutingActor.class, size);
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof WorkerReportTaskStatusRequest) {
            WorkerReportTaskStatusRequest request = (WorkerReportTaskStatusRequest) obj;
            String fullId = request.getFullId();
            int hashcode = fullId.hashCode();
            actors.get((hashcode == Integer.MIN_VALUE ? 0 : Math.abs(hashcode)) % size).forward(obj, getContext());
        } else if (obj instanceof WorkerReportTaskProgressRequest) {
            WorkerReportTaskProgressRequest request = (WorkerReportTaskProgressRequest) obj;
            String fullId = request.getFullId();
            int hashcode = fullId.hashCode();
            actors.get((hashcode == Integer.MIN_VALUE ? 0 : Math.abs(hashcode)) % size).forward(obj, getContext());
        } else {
            if (index >= size) {
                index = 0;
            }
            actors.get(index++).forward(obj, getContext());
        }
    }

    public static List<ActorEntry> handledMessages() {
        return TaskMetricsActor.handledMessages();
    }

}
