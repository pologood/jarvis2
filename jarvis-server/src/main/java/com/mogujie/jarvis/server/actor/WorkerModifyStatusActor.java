/*
 * 蘑菇街 Inc. Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya Create Date: 2015年9月22日 下午3:25:50
 */

package com.mogujie.jarvis.server.actor;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.javassist.NotFoundException;

import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.RestServerModifyWorkerStatusRequest;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.ServerModifyWorkerStatusResponse;
import com.mogujie.jarvis.server.domain.ActorEntry;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.service.WorkerService;

import akka.actor.Props;
import akka.actor.UntypedActor;

public class WorkerModifyStatusActor extends UntypedActor {

    private WorkerService workerService = Injectors.getInjector().getInstance(WorkerService.class);

    public static Props props() {
        return Props.create(WorkerModifyStatusActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestServerModifyWorkerStatusRequest) {
            updateStatus((RestServerModifyWorkerStatusRequest) obj);
        } else {
            unhandled(obj);
        }
    }

    public void updateStatus(RestServerModifyWorkerStatusRequest request) {
        ServerModifyWorkerStatusResponse response;
        try {
            String ip = request.getIp();
            int port = request.getPort();
            int status = request.getStatus();
            int workerId = workerService.getWorkerId(ip, port);
            if (workerId == 0) {
                throw new NotFoundException("worker不存在。ip:" + ip + ";port:" + port);
            }
            workerService.updateWorkerStatus(workerId, status);
            response = ServerModifyWorkerStatusResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } catch (Exception ex) {
            response = ServerModifyWorkerStatusResponse.newBuilder().setSuccess(false).setMessage(ex.getMessage()).build();
            getSender().tell(response, getSelf());
        }
    }

    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(RestServerModifyWorkerStatusRequest.class, ServerModifyWorkerStatusResponse.class, MessageType.SYSTEM));
        return list;
    }

}
