/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月18日 下午3:19:28
 */
package com.mogujie.jarvis.rest.control;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import com.mogujie.jarvis.core.domain.WorkerStatus;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.*;

import com.mogujie.jarvis.rest.RestResult;

import akka.actor.ActorSystem;


/**
 * @author muming
 *
 */
@Path("worker")
public class SysControl extends AbstractControl {

    public SysControl(ActorSystem system, String serverAkkaPath, String workerAkkaPath) {
        super(system, serverAkkaPath, workerAkkaPath);
    }


    @POST
    @Path("onlineWorker")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult onlineClient(@FormParam("appKey") String appKey,
                                   @FormParam("appName") String appName,
                                   @FormParam("ip") String ip,
                                   @FormParam("port") int port,
                                   @FormParam("status") int status) throws Exception
    {

        WorkerStatus ws = (status == 1 ) ? WorkerStatus.ONLINE : WorkerStatus.OFFLINE;

        RestServerModifyWorkerStatusRequest request = RestServerModifyWorkerStatusRequest.newBuilder()
                .setIp(ip)
                .setPort(port)
                .setStatus(ws.getValue())
                .build();

        ServerModifyWorkerStatusResponse response = (ServerModifyWorkerStatusResponse) callServerActor(request);

        if(response.getSuccess()){
            return successResult();
        }else{
            return errorResult("msg");
        }

    }




}
