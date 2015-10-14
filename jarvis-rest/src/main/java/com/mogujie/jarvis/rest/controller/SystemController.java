/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月18日 下午3:19:28
 */
package com.mogujie.jarvis.rest.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.core.domain.WorkerStatus;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.RestServerModifyWorkerStatusRequest;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.ServerModifyWorkerStatusResponse;
import com.mogujie.jarvis.rest.RestResult;

/**
 * @author muming
 *
 */
@Path("system")
public class SystemController extends AbstractController {

    @POST
    @Path("setWorkerStatus")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult onlineClient(@FormParam("appKey") String appKey, @FormParam("appName") String appName, @FormParam("ip") String ip,
            @FormParam("port") int port, @FormParam("status") int status) throws Exception {
        WorkerStatus ws = (status == 1) ? WorkerStatus.ONLINE : WorkerStatus.OFFLINE;

        RestServerModifyWorkerStatusRequest request = RestServerModifyWorkerStatusRequest.newBuilder().setIp(ip).setPort(port)
                .setStatus(ws.getValue()).build();

        ServerModifyWorkerStatusResponse response = (ServerModifyWorkerStatusResponse) callActor(AkkaType.SERVER, request);

        if (response.getSuccess()) {
            return successResult();
        } else {
            return errorResult(response.getMessage());
        }

    }

}
