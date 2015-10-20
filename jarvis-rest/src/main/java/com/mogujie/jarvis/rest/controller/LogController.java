/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: muming
 * Create Date: 2015年10月08日 下午3:19:28
 */
package com.mogujie.jarvis.rest.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.protocol.ReadLogProtos.LogServerReadLogResponse;
import com.mogujie.jarvis.protocol.ReadLogProtos.RestServerReadLogRequest;
import com.mogujie.jarvis.rest.RestResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

/**
 * @author muming
 *
 */
@Path("log")
public class LogController extends AbstractController {

    /**
     * 获取执行日志
     *
     * @param appName
     *            appName
     * @param appToken
     *            taskId
     * @param user
     *
     * @param parameters (taskId、offset：日志内容的字节偏移量、lines：日志读取的行数)
     *
     * @return
     * @throws Exception
     */
    @POST
    @Path("queryExecuteLog")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult queryExecuteLog(@FormParam("appToken") String appToken,
                                      @FormParam("appName") String appName,
                                      @FormParam("user") String user,
                                      @FormParam("parameters") String parameters){

        try {
            JSONObject para=new JSONObject(parameters);
            Long taskId=para.getLong("taskId");
            Long offset=para.getLong("offset");
            Integer lines=para.getInt("lines");

            RestServerReadLogRequest request = RestServerReadLogRequest.newBuilder().setTaskId(taskId).setType(StreamType.STD_ERR.getValue())
                    .setOffset(offset).setLines(lines).build();

            LogServerReadLogResponse response = (LogServerReadLogResponse) callActor(AkkaType.LOGSTORAGE, request);

            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult("msg");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return errorResult(e.getMessage());
        }
    }

    /**
     * 获取结果数据
     *
     * @param appName
     *            appName
     * @param appToken
     *
     * @param user
     *
     * @param parameters (taskId、offset：日志内容的字节偏移量、lines：日志读取的行数)
     *
     * @return
     * @throws Exception
     */
    @POST
    @Path("queryResult")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult queryResult(@FormParam("appToken") String appToken,
                                  @FormParam("appName") String appName,
                                  @FormParam("user") String user,
                                  @FormParam("parameters") String parameters) throws Exception {
        try {
            JSONObject para=new JSONObject(parameters);
            Long taskId=para.getLong("taskId");
            Long offset=para.getLong("offset");
            Integer lines=para.getInt("lines");

            RestServerReadLogRequest request = RestServerReadLogRequest.newBuilder().setTaskId(taskId).setType(StreamType.STD_OUT.getValue())
                    .setOffset(offset).setLines(lines).build();

            LogServerReadLogResponse response = (LogServerReadLogResponse) callActor(AkkaType.LOGSTORAGE, request);

            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return  errorResult(e.getMessage());
        }
    }

}
