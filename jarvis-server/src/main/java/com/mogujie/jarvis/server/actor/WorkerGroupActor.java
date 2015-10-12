/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年10月12日 上午10:18:24
 */

package com.mogujie.jarvis.server.actor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.mogujie.jarvis.dao.WorkerGroupMapper;
import com.mogujie.jarvis.dto.WorkerGroup;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.RestServerCreateWorkerGroupRequest;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.RestServerModifyWorkerGroupRequest;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.ServerCreateWorkerGroupResponse;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.ServerModifyWorkerGroupResponse;

import akka.actor.UntypedActor;

/**
 * 
 *
 */
@Named("workerGroupActor")
@Scope("prototype")
public class WorkerGroupActor extends UntypedActor {

    @Autowired
    private WorkerGroupMapper workerGroupMapper;

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestServerCreateWorkerGroupRequest) {
            RestServerCreateWorkerGroupRequest request = (RestServerCreateWorkerGroupRequest) obj;
            WorkerGroup workerGroup = new WorkerGroup();
            String key = UUID.randomUUID().toString().replace("-", "");
            workerGroup.setKey(key);
            workerGroup.setName(request.getWorkerGroupName());
            Date date = new Date();
            workerGroup.setCreateTime(date);
            workerGroup.setUpdateTime(date);
            workerGroup.setCreator(request.getUser());
            workerGroupMapper.insertSelective(workerGroup);

            ServerCreateWorkerGroupResponse response = ServerCreateWorkerGroupResponse.newBuilder().setSuccess(true).setWorkerGroupKey(key).build();
            getSender().tell(response, getSelf());
        } else if (obj instanceof RestServerModifyWorkerGroupRequest) {
            RestServerModifyWorkerGroupRequest request = (RestServerModifyWorkerGroupRequest) obj;
            WorkerGroup workerGroup = new WorkerGroup();
            workerGroup.setId(request.getWorkerGroupId());
            workerGroup.setName(request.getWorkerGroupName());
            workerGroup.setUpdateTime(new Date());
            workerGroupMapper.updateByPrimaryKey(workerGroup);

            ServerModifyWorkerGroupResponse response = ServerModifyWorkerGroupResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } else {
            unhandled(obj);
        }
    }

    public static Set<Class<?>> handledMessages() {
        Set<Class<?>> set = new HashSet<>();
        set.add(RestServerCreateWorkerGroupRequest.class);
        set.add(RestServerModifyWorkerGroupRequest.class);
        return set;
    }

}
