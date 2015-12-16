/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya Create Date: 2015年10月12日 上午10:18:24
 */

package com.mogujie.jarvis.server.actor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.dto.generate.WorkerGroup;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.RestServerCreateWorkerGroupRequest;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.RestServerModifyWorkerGroupRequest;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.ServerCreateWorkerGroupResponse;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.ServerModifyWorkerGroupResponse;
import com.mogujie.jarvis.server.domain.ActorEntry;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.service.WorkerGroupService;

import akka.actor.Props;
import akka.actor.UntypedActor;

public class WorkerGroupActor extends UntypedActor {

    private WorkerGroupService workerGroupService = Injectors.getInjector().getInstance(WorkerGroupService.class);

    public static Props props() {
        return Props.create(WorkerGroupActor.class);
    }

    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(RestServerCreateWorkerGroupRequest.class, ServerCreateWorkerGroupResponse.class, MessageType.SYSTEM));
        list.add(new ActorEntry(RestServerModifyWorkerGroupRequest.class, ServerModifyWorkerGroupResponse.class, MessageType.SYSTEM));
        return list;
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestServerCreateWorkerGroupRequest) {
            createWorkerGroup((RestServerCreateWorkerGroupRequest) obj);
        } else if (obj instanceof RestServerModifyWorkerGroupRequest) {
            modifyWorkerGroup((RestServerModifyWorkerGroupRequest) obj);
        } else {
            unhandled(obj);
        }
    }

    public void createWorkerGroup(RestServerCreateWorkerGroupRequest request) {
        ServerCreateWorkerGroupResponse response;
        try {
            WorkerGroup workerGroup = new WorkerGroup();
            String key = UUID.randomUUID().toString().replace("-", "");
            workerGroup.setAuthKey(key);
            workerGroup.setName(request.getWorkerGroupName());
            DateTime now = DateTime.now();
            workerGroup.setCreateTime(now.toDate());
            workerGroup.setUpdateTime(now.toDate());
            workerGroup.setUpdateUser(request.getUser());
            workerGroupService.insert(workerGroup);

            response = ServerCreateWorkerGroupResponse.newBuilder().setSuccess(true).setWorkerGroupKey(key).build();
            getSender().tell(response, getSelf());
        } catch (Exception ex) {
            response = ServerCreateWorkerGroupResponse.newBuilder().setSuccess(false).setMessage(ex.getMessage()).build();
            getSender().tell(response, getSelf());
        }
    }

    public void modifyWorkerGroup(RestServerModifyWorkerGroupRequest request) {
        ServerModifyWorkerGroupResponse response;
        try {
            WorkerGroup workerGroup = new WorkerGroup();
            workerGroup.setId(request.getWorkerGroupId());
            if (request.hasWorkerGroupName()) {
                workerGroup.setName(request.getWorkerGroupName());
            }
            if (request.hasUser()) {
                workerGroup.setUpdateUser(request.getUser());
            }
            if (request.hasStatus()) {
                workerGroup.setStatus(request.getStatus());
            }
            workerGroup.setUpdateTime(DateTime.now().toDate());
            workerGroup.setUpdateUser(request.getUser());
            workerGroupService.update(workerGroup);
            response = ServerModifyWorkerGroupResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } catch (Exception ex) {
            response = ServerModifyWorkerGroupResponse.newBuilder().setSuccess(false).setMessage(ex.getMessage()).build();
            getSender().tell(response, getSelf());
        }
    }

}
