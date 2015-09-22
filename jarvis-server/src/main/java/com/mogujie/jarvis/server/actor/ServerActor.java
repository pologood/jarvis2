/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月21日 下午4:13:54
 */

package com.mogujie.jarvis.server.actor;

import com.mogujie.jarvis.protocol.HeartBeatProtos.HeartBeatRequest;
import com.mogujie.jarvis.protocol.KillTaskProtos.RestServerKillTaskRequest;
import com.mogujie.jarvis.protocol.ModifyJobFlagProtos.RestServerModifyJobFlagRequest;
import com.mogujie.jarvis.protocol.ModifyJobProtos.RestServerModifyJobRequest;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.RestServerModifyWorkerStatusRequest;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos.WorkerRegistryRequest;
import com.mogujie.jarvis.protocol.ReportProgressProtos.WorkerReportProgressRequest;
import com.mogujie.jarvis.protocol.ReportStatusProtos.WorkerReportStatusRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;
import com.mogujie.jarvis.server.util.SpringExtension;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.SmallestMailboxPool;

/**
 * ServerActor forward any messages to other actors
 *
 */
public class ServerActor extends UntypedActor {

    private ActorRef taskMetricsActor = getContext()
            .actorOf(SpringExtension.SPRING_EXT_PROVIDER.get(getContext().system()).props("taskMetricsActor"));
    private ActorRef heartBeatActor = getContext().actorOf(SpringExtension.SPRING_EXT_PROVIDER.get(getContext().system()).props("heartBeatActor"));
    private ActorRef workerRegistryActor = getContext()
            .actorOf(SpringExtension.SPRING_EXT_PROVIDER.get(getContext().system()).props("workerRegistryActor"));
    private ActorRef jobSchedulerActor = getContext().actorOf(
            SpringExtension.SPRING_EXT_PROVIDER.get(getContext().system()).props("jobSchedulerActor").withRouter(new SmallestMailboxPool(10)));

    private ActorRef killTaskActor = getContext()
            .actorOf(SpringExtension.SPRING_EXT_PROVIDER.get(getContext().system()).props("killTaskActor").withRouter(new SmallestMailboxPool(10)));
    private ActorRef modifyJobFlagActor = getContext()
            .actorOf(SpringExtension.SPRING_EXT_PROVIDER.get(getContext().system()).props("modifyJobFlagActor"));
    private ActorRef modifyJobActor = getContext().actorOf(SpringExtension.SPRING_EXT_PROVIDER.get(getContext().system()).props("modifyJobActor"));
    private ActorRef modifyWorkerStatusActor = getContext()
            .actorOf(SpringExtension.SPRING_EXT_PROVIDER.get(getContext().system()).props("modifyWorkerStatusActor"));

    public static Props props() {
        return Props.create(ServerActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof HeartBeatRequest) {
            heartBeatActor.forward(obj, getContext());
        } else if (obj instanceof RestServerSubmitJobRequest || obj instanceof RestServerModifyJobRequest
                || obj instanceof RestServerModifyJobFlagRequest) {
            jobSchedulerActor.forward(obj, getContext());
        } else if (obj instanceof WorkerReportStatusRequest) {
            jobSchedulerActor.forward(obj, getContext());
            taskMetricsActor.forward(obj, getContext());
        } else if (obj instanceof WorkerReportProgressRequest) {
            taskMetricsActor.forward(obj, getContext());
        } else if (obj instanceof WorkerRegistryRequest) {
            workerRegistryActor.forward(obj, getContext());
        } else if (obj instanceof RestServerKillTaskRequest) {
            killTaskActor.forward(obj, getContext());
        } else if (obj instanceof RestServerModifyJobFlagRequest) {
            modifyJobFlagActor.forward(obj, getContext());
        } else if (obj instanceof RestServerModifyJobRequest) {
            modifyJobActor.forward(obj, getContext());
        } else if (obj instanceof RestServerModifyWorkerStatusRequest) {
            modifyWorkerStatusActor.forward(obj, getContext());
        } else {
            unhandled(obj);
        }
    }

}
