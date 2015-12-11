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
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.SystemStatusProtos.RestServerUpdateSystemStatusRequest;
import com.mogujie.jarvis.protocol.SystemStatusProtos.ServerUpdateSystemStatusResponse;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.utils.JsonParameters;

/**
 * @author muming
 *
 */
@Path("api/system")
public class SystemController extends AbstractController {

    /**
     * 修改系统状态
     * 
     * @author hejian
     */
    @POST
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult status(@FormParam("user") String user, @FormParam("appToken") String appToken, @FormParam("appName") String appName,
            @FormParam("parameters") String parameters) {
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JsonParameters para = new JsonParameters(parameters);

            int status = para.getIntegerNotNull("status");
            RestServerUpdateSystemStatusRequest request = RestServerUpdateSystemStatusRequest.newBuilder().setAppAuth(appAuth).setStatus(status)
                    .build();
            ServerUpdateSystemStatusResponse response = (ServerUpdateSystemStatusResponse) callActor(AkkaType.SERVER, request);

            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            return errorResult(e.getMessage());
        }
    }

}
