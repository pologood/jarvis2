/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月22日 上午9:55:19
 */

package com.mogujie.jarvis.server.actor;

import org.springframework.stereotype.Service;

import com.mogujie.jarvis.core.domain.WorkerInfo;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos.ServerRegistryResponse;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos.WorkerRegistryRequest;
import com.mogujie.jarvis.server.WorkerRegistry;

import akka.actor.Address;
import akka.actor.UntypedActor;

/**
 * Worker authentication
 *
 */
@Service
public class WorkerRegistryActor extends UntypedActor {

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof WorkerRegistryRequest) {
            WorkerRegistryRequest request = (WorkerRegistryRequest) obj;
            String key = request.getKey();
            int groupId = request.getGroupId();
            boolean valid = false;
            ServerRegistryResponse response = null;
            // TODO worker注册验证
            if (valid) {
                Address address = getSender().path().address();
                String ip = address.host().get();
                int port = Integer.parseInt(address.port().get().toString());
                WorkerInfo workerInfo = new WorkerInfo(ip, port);
                WorkerRegistry workerRegistry = WorkerRegistry.getInstance();
                workerRegistry.put(workerInfo, groupId);
                response = ServerRegistryResponse.newBuilder().setSuccess(true).build();
            } else {
                response = ServerRegistryResponse.newBuilder().setSuccess(false).setMessage("Worker authentication failed").build();
            }

            getSender().tell(response, getSelf());
        } else {
            unhandled(obj);
        }
    }

}
