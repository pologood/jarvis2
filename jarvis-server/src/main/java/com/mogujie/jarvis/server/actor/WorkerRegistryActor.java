/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月22日 上午9:55:19
 */

package com.mogujie.jarvis.server.actor;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.mogujie.jarvis.core.domain.WorkerInfo;
import com.mogujie.jarvis.core.domain.WorkerStatus;
import com.mogujie.jarvis.dao.WorkerGroupMapper;
import com.mogujie.jarvis.dao.WorkerMapper;
import com.mogujie.jarvis.dto.Worker;
import com.mogujie.jarvis.dto.WorkerGroup;
import com.mogujie.jarvis.dto.WorkerGroupExample;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos.ServerRegistryResponse;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos.WorkerRegistryRequest;
import com.mogujie.jarvis.server.WorkerRegistry;

import akka.actor.Address;
import akka.actor.UntypedActor;

/**
 * Worker authentication
 *
 */
@Named("workerRegistryActor")
@Scope("prototype")
public class WorkerRegistryActor extends UntypedActor {

    @Autowired
    private WorkerGroupMapper workerGroupMapper;

    @Autowired
    private WorkerMapper workerMapper;

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof WorkerRegistryRequest) {
            WorkerRegistryRequest request = (WorkerRegistryRequest) obj;
            String key = request.getKey();

            WorkerGroupExample example = new WorkerGroupExample();
            example.createCriteria().andAuthKeyEqualTo(key);
            List<WorkerGroup> list = workerGroupMapper.selectByExample(example);

            boolean valid = false;
            int groupId = 0;
            if (list != null && list.size() > 0) {
                groupId = list.get(0).getId();
                valid = true;
            }

            ServerRegistryResponse response = null;
            if (valid) {
                Address address = getSender().path().address();
                String ip = address.host().get();
                int port = Integer.parseInt(address.port().get().toString());
                WorkerInfo workerInfo = new WorkerInfo(ip, port);
                WorkerRegistry workerRegistry = WorkerRegistry.getInstance();
                workerRegistry.put(workerInfo, groupId);
                response = ServerRegistryResponse.newBuilder().setSuccess(true).build();

                Worker worker = new Worker();
                worker.setIp(ip);
                worker.setPort(port);
                worker.setWorkerGroupId(groupId);
                worker.setStatus(WorkerStatus.ONLINE.getValue());
                Timestamp ts = new Timestamp(DateTime.now().getMillis());
                worker.setCreateTime(ts);
                worker.setUpdateTime(ts);
                workerMapper.insert(worker);
            } else {
                response = ServerRegistryResponse.newBuilder().setSuccess(false).setMessage("Invaild worker group key").build();
            }

            getSender().tell(response, getSelf());
        } else {
            unhandled(obj);
        }
    }

    public static Set<Class<?>> handledMessages() {
        Set<Class<?>> set = new HashSet<>();
        set.add(WorkerRegistryRequest.class);
        return set;
    }

}
