/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya Create Date: 2015年9月22日 上午9:55:19
 */

package com.mogujie.jarvis.server.actor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.google.common.base.Preconditions;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.core.domain.WorkerInfo;
import com.mogujie.jarvis.core.domain.WorkerStatus;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos.ServerRegistryResponse;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos.WorkerRegistryRequest;
import com.mogujie.jarvis.server.WorkerRegistry;
import com.mogujie.jarvis.server.domain.ActorEntry;
import com.mogujie.jarvis.server.service.WorkerService;

import akka.actor.Address;
import akka.actor.UntypedActor;

/**
 * Worker authentication
 */
@Named("workerRegistryActor")
@Scope("prototype")
public class WorkerRegistryActor extends UntypedActor {

    @Autowired
    private WorkerService workerService;

    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(WorkerRegistryRequest.class, ServerRegistryResponse.class, MessageType.SYSTEM));
        return list;
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof WorkerRegistryRequest) {
            WorkerRegistry((WorkerRegistryRequest) obj);
        } else {
            unhandled(obj);
        }
    }

    private void WorkerRegistry(WorkerRegistryRequest request) {
        ServerRegistryResponse response;
        try {

            String key = request.getKey();
            int groupId = workerService.getGroupIdByAuthKey(key);
            Preconditions.checkArgument(groupId != 0, "invaild worker group key");

            Address address = getSender().path().address();
            String ip = address.host().get();
            int port = Integer.parseInt(address.port().get().toString());
            WorkerInfo workerInfo = new WorkerInfo(ip, port);
            WorkerRegistry workerRegistry = WorkerRegistry.getInstance();
            workerRegistry.put(workerInfo, groupId);

            workerService.saveWorker(ip, port, groupId, WorkerStatus.ONLINE.getValue());

            response = ServerRegistryResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());

        } catch (Exception ex) {
            response = ServerRegistryResponse.newBuilder().setSuccess(false).setMessage(ex.getMessage()).build();
            getSender().tell(response, getSelf());
        }
    }

}
