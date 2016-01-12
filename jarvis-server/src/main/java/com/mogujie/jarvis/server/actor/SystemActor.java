/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya Create Date: 2015年10月12日 上午11:14:54
 */

package com.mogujie.jarvis.server.actor;

import java.util.ArrayList;
import java.util.List;

import akka.actor.Props;
import akka.actor.UntypedActor;

import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.protocol.SystemStatusProtos.RestServerUpdateSystemStatusRequest;
import com.mogujie.jarvis.protocol.SystemStatusProtos.ServerUpdateSystemStatusResponse;
import com.mogujie.jarvis.server.dispatcher.TaskDispatcher;
import com.mogujie.jarvis.server.domain.ActorEntry;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;

public class SystemActor extends UntypedActor {

    private TaskDispatcher taskDispatcher = Injectors.getInjector().getInstance(TaskDispatcher.class);
    private JobSchedulerController controller = JobSchedulerController.getInstance();

    public static Props props() {
        return Props.create(SystemActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestServerUpdateSystemStatusRequest) {
            RestServerUpdateSystemStatusRequest request = (RestServerUpdateSystemStatusRequest) obj;
            if (request.getStatus() > 0) {
                taskDispatcher.restart();
                controller.notify(new StartEvent());
            } else {
                taskDispatcher.pause();
                controller.notify(new StopEvent());
            }

            ServerUpdateSystemStatusResponse response = ServerUpdateSystemStatusResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } else {
            unhandled(obj);
        }
    }

    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(RestServerUpdateSystemStatusRequest.class, ServerUpdateSystemStatusResponse.class, MessageType.SYSTEM));
        return list;
    }

}
