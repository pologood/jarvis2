/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年3月24日 上午11:33:50
 */

package com.mogujie.jarvis.server.actor;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import akka.actor.Props;
import akka.actor.UntypedActor;

import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.protocol.DepartmentProtos.RestCreateDepartmentBizMapRequest;
import com.mogujie.jarvis.protocol.DepartmentProtos.RestCreateDepartmentRequest;
import com.mogujie.jarvis.protocol.DepartmentProtos.RestDeleteDepartmentBizMapRequest;
import com.mogujie.jarvis.protocol.DepartmentProtos.RestDeleteDepartmentRequest;
import com.mogujie.jarvis.protocol.DepartmentProtos.RestModifyDepartmentRequest;
import com.mogujie.jarvis.protocol.DepartmentProtos.ServerCreateDepartmentBizMapResponse;
import com.mogujie.jarvis.protocol.DepartmentProtos.ServerCreateDepartmentResponse;
import com.mogujie.jarvis.protocol.DepartmentProtos.ServerDeleteDepartmentBizMapResponse;
import com.mogujie.jarvis.protocol.DepartmentProtos.ServerDeleteDepartmentResponse;
import com.mogujie.jarvis.protocol.DepartmentProtos.ServerModifyDepartmentResponse;
import com.mogujie.jarvis.server.domain.ActorEntry;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.service.DepartmentService;

/**
 * @author guangming
 *
 */
public class DepartmentActor extends UntypedActor {

    private static final Logger LOGGER = LogManager.getLogger();

    private DepartmentService departmentService = Injectors.getInjector().getInstance(DepartmentService.class);

    public static Props props() {
        return Props.create(DepartmentActor.class);
    }

    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(RestCreateDepartmentRequest.class, ServerCreateDepartmentResponse.class, MessageType.SYSTEM));
        list.add(new ActorEntry(RestModifyDepartmentRequest.class, ServerModifyDepartmentResponse.class, MessageType.SYSTEM));
        list.add(new ActorEntry(RestDeleteDepartmentRequest.class, ServerDeleteDepartmentResponse.class, MessageType.SYSTEM));
        list.add(new ActorEntry(RestCreateDepartmentBizMapRequest.class, ServerCreateDepartmentBizMapResponse.class, MessageType.SYSTEM));
        list.add(new ActorEntry(RestDeleteDepartmentBizMapRequest.class, ServerDeleteDepartmentBizMapResponse.class, MessageType.SYSTEM));
        return list;
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestCreateDepartmentRequest) {
            createDepartment((RestCreateDepartmentRequest) obj);
        } else if (obj instanceof RestModifyDepartmentRequest) {
            modifyDepartment((RestModifyDepartmentRequest) obj);
        } else if (obj instanceof RestDeleteDepartmentRequest) {
            deleteDepartment((RestDeleteDepartmentRequest) obj);
        } else if (obj instanceof RestCreateDepartmentBizMapRequest) {
            createDeparmentBizMap((RestCreateDepartmentBizMapRequest) obj);
        } else if (obj instanceof RestDeleteDepartmentBizMapRequest) {
            deleteDepartmentBizMap((RestDeleteDepartmentBizMapRequest) obj);
        } else {
            unhandled(obj);
        }

    }

    private void createDepartment(RestCreateDepartmentRequest request) {
        //TODO
    }

    private void modifyDepartment(RestModifyDepartmentRequest request) {
        //TODO
    }

    private void deleteDepartment(RestDeleteDepartmentRequest request) {
        //TODO
    }

    private void createDeparmentBizMap(RestCreateDepartmentBizMapRequest request) {
        //TODO
    }

    private void deleteDepartmentBizMap(RestDeleteDepartmentBizMapRequest request) {
        //TODO
    }

}
