/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月18日 下午3:19:28
 */
package com.mogujie.jarvis.rest.controller;

import akka.actor.ActorSystem;
import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.protocol.ReadLogProtos.*;
import com.mogujie.jarvis.rest.RestResult;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * @author muming
 *
 */
@Path("log")
public class LogController extends AbstractController {

    public LogController(ActorSystem system, String serverAkkaPath, String workerAkkaPath) {
        super(system, serverAkkaPath, workerAkkaPath);
    }


    /**
     * 获取执行日志
     *
     * @param appKey        appKey
     * @param appName       appName
     * @param taskId        taskId
     * @param offset        日志内容的字节偏移量
     * @param lines         日志读取的行数
     * @return
     * @throws Exception
     */
    @POST
    @Path("queryExecuteLog")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult queryExecuteLog(@FormParam("appKey") String appKey,
                                   @FormParam("appName") String appName,
                                   @FormParam("taskId") long taskId,
                                   @FormParam("offset") long offset,
                                   @FormParam("lines") int lines
    ) throws Exception
    {


        RestServerReadLogRequest request = RestServerReadLogRequest.newBuilder()
                .setTaskId(taskId)
                .setType(StreamType.STD_ERR.getValue())
                .setOffset(offset)
                .setLines(lines)
                .build();

        LogServerReadLogResponse response = (LogServerReadLogResponse) callActor(logCenterActor, request);

        if(response.getSuccess()){
            return successResult();
        }else{
            return errorResult("msg");
        }

    }


    /**
     * 获取结果数据
     *
     * @param appKey        appKey
     * @param appName       appName
     * @param taskId        taskId
     * @param offset        日志内容的字节偏移量
     * @param lines         日志读取的行数
     * @return
     * @throws Exception
     */
    @POST
    @Path("queryResult")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult queryResult(@FormParam("appKey") String appKey,
                                   @FormParam("appName") String appName,
                                   @FormParam("taskId") long taskId,
                                   @FormParam("offset") long offset,
                                   @FormParam("lines") int lines
    ) throws Exception
    {


        RestServerReadLogRequest request = RestServerReadLogRequest.newBuilder()
                .setTaskId(taskId)
                .setType(StreamType.STD_OUT.getValue())
                .setOffset(offset)
                .setLines(lines)
                .build();

        LogServerReadLogResponse response = (LogServerReadLogResponse) callActor(logCenterActor, request);

        if(response.getSuccess()){
            return successResult();
        }else{
            return errorResult(response.getMessage());
        }

    }








}
