/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月18日 下午3:19:28
 */
package com.mogujie.jarvis.rest.control;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;


import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.*;

import org.glassfish.jersey.client.ClientResponse;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @author wuya
 *
 */
@Path("server")
public class RestResource {

    private ActorSystem system;
    private String serverAkkaPath;
    private String logServerAkkaPath;

    private static final Timeout TIMEOUT = new Timeout(Duration.create(30, TimeUnit.SECONDS));
    private static final Logger LOGGER = LogManager.getLogger();

    public RestResource(ActorSystem system, String serverAkkaPath, String logServerAkkaPath) {
        this.system = system;
        this.serverAkkaPath = serverAkkaPath;
        this.logServerAkkaPath = logServerAkkaPath;
    }


    @POST
    @Path("onlineclient")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> onlineClient(@QueryParam("appKey") String appKey,
                                            @FormParam("appName") String appName,
                                            @FormParam("ip") String ip,
                                            @FormParam("port") int port
    )
    {

        RestResponse restResponse = RestResponse.create();
        ActorSelection actor = system.actorSelection(serverAkkaPath + "/user/server");
        RestServerModifyWorkerStatusRequest request = RestServerModifyWorkerStatusRequest.newBuilder()
                .setIp(ip).setPort(port).setStatus(true).build();
        Future<Object> future = Patterns.ask(actor, request, TIMEOUT);
        try {
            ClientResponse response = (ClientResponse) Await.result(future, TIMEOUT.duration());
            restResponse.setRespCode(0);
            restResponse.setRespMsg("");
            restResponse.setSuccess(response.getSuccess());
            restResponse.setWrongParams(false);
        } catch (Exception e) {
            restResponse.setRespCode(1);
            restResponse.setRespMsg(e.getMessage());
            restResponse.setSuccess(false);
            restResponse.setWrongParams(true);
            LOGGER.error("", e);
        }

        return restResponse.toMap();
    }







}
