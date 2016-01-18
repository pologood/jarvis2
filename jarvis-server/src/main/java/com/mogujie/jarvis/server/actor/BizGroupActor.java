/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya Create Date: 2015年10月12日 上午10:18:24
 */

package com.mogujie.jarvis.server.actor;

import akka.actor.Props;
import akka.actor.UntypedActor;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.protocol.BizGroupProtos.RestCreateBizGroupRequest;
import com.mogujie.jarvis.protocol.BizGroupProtos.RestModifyBizGroupRequest;
import com.mogujie.jarvis.protocol.BizGroupProtos.ServerCreateBizGroupResponse;
import com.mogujie.jarvis.protocol.BizGroupProtos.ServerModifyBizGroupResponse;
import com.mogujie.jarvis.server.domain.ActorEntry;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.service.BizGroupService;
import com.mogujie.jarvis.server.service.ConvertValidService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BizGroupActor extends UntypedActor {
    private static final Logger logger = LogManager.getLogger();

    private BizGroupService BizGroupService = Injectors.getInjector().getInstance(BizGroupService.class);
    private ConvertValidService convertValidService = Injectors.getInjector().getInstance(ConvertValidService.class);

    public static Props props() {
        return Props.create(BizGroupActor.class);
    }

    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(RestCreateBizGroupRequest.class, ServerCreateBizGroupResponse.class, MessageType.SYSTEM));
        list.add(new ActorEntry(RestModifyBizGroupRequest.class, ServerModifyBizGroupResponse.class, MessageType.SYSTEM));
        return list;
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestCreateBizGroupRequest) {
            createBizGroup((RestCreateBizGroupRequest) obj);
        } else if (obj instanceof RestModifyBizGroupRequest) {
            modifyBizGroup((RestModifyBizGroupRequest) obj);
        } else {
            unhandled(obj);
        }
    }

    public void createBizGroup(RestCreateBizGroupRequest request) {
        ServerCreateBizGroupResponse response;
        try {

            BizGroup BizGroup = convertValidService.convert2AppWorkeGroupByCheck(request);
            String key = UUID.randomUUID().toString().replace("-", "");
            BizGroup.setAuthKey(key);
            BizGroup.setName(request.getBizGroupName());
            DateTime now = DateTime.now();
            BizGroup.setCreateTime(now.toDate());
            BizGroup.setUpdateTime(now.toDate());
            BizGroup.setUpdateUser(request.getUser());
            BizGroupService.insert(BizGroup);

            response = ServerCreateBizGroupResponse.newBuilder().setSuccess(true).setBizGroupKey(key).build();
            getSender().tell(response, getSelf());
        } catch (Exception ex) {
            response = ServerCreateBizGroupResponse.newBuilder().setSuccess(false).setMessage(ex.getMessage()).build();
            getSender().tell(response, getSelf());
        }
    }

    public void modifyBizGroup(RestModifyBizGroupRequest request) {
        ServerModifyBizGroupResponse response;
        try {
            BizGroup BizGroup = new BizGroup();
            BizGroup.setId(request.getBizGroupId());
            if (request.hasBizGroupName()) {
                BizGroup.setName(request.getBizGroupName());
            }
            if (request.hasUser()) {
                BizGroup.setUpdateUser(request.getUser());
            }
            if (request.hasStatus()) {
                BizGroup.setStatus(request.getStatus());
            }
            BizGroup.setUpdateTime(DateTime.now().toDate());
            BizGroup.setUpdateUser(request.getUser());
            BizGroupService.update(BizGroup);
            response = ServerModifyBizGroupResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } catch (Exception ex) {
            response = ServerModifyBizGroupResponse.newBuilder().setSuccess(false).setMessage(ex.getMessage()).build();
            getSender().tell(response, getSelf());
        }
    }

}
