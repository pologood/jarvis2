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
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.SystemStatusProtos.RestServerUpdateSystemStatusRequest;
import com.mogujie.jarvis.protocol.SystemStatusProtos.ServerUpdateSystemStatusResponse;
import com.mogujie.jarvis.rest.RestResult;

/**
 * @author muming
 *
 */
@Path("system")
public class SystemController extends AbstractController {

    @POST
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult status(@FormParam("user") String user,
                             @FormParam("appToken") String appToken,
                             @FormParam("appName") String appName,
                             @FormParam("appKey") String appKey,
                             @FormParam("status") int status) {
        try {
            //WorkerStatus ws = (status == 1) ? WorkerStatus.ONLINE : WorkerStatus.OFFLINE;
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();
            RestServerUpdateSystemStatusRequest request = RestServerUpdateSystemStatusRequest.newBuilder()
                                                .setAppAuth(appAuth).setStatus(status).build();
            ServerUpdateSystemStatusResponse response = (ServerUpdateSystemStatusResponse) callActor(AkkaType.SERVER, request);

            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return errorResult(e.getMessage());
        }
    }

}
