/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月22日 下午3:25:50
 */

package com.mogujie.jarvis.server.actor;

import javax.inject.Named;

import org.springframework.context.annotation.Scope;

import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.RestServerModifyWorkerStatusRequest;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.ServerModifyWorkerStatusResponse;

import akka.actor.UntypedActor;

/**
 * 
 *
 */
@Named("modifyWorkerStatusActor")
@Scope("prototype")
public class ModifyWorkerStatusActor extends UntypedActor {

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestServerModifyWorkerStatusRequest) {
            RestServerModifyWorkerStatusRequest request = (RestServerModifyWorkerStatusRequest) obj;
            String ip = request.getIp();
            int port = request.getPort();
            int status = request.getStatus();

            // TODO 修改Worker状态

            ServerModifyWorkerStatusResponse response = ServerModifyWorkerStatusResponse.newBuilder().setSuccess(true).setMessage("").build();
            getSender().tell(response, getSelf());
        } else {
            unhandled(obj);
        }
    }

}
