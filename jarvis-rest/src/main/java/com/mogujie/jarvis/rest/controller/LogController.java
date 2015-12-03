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

import org.json.JSONObject;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.protocol.AppAuthProtos;
import com.mogujie.jarvis.protocol.ReadLogProtos.LogServerReadLogResponse;
import com.mogujie.jarvis.protocol.ReadLogProtos.RestServerReadLogRequest;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.vo.LogVo;

/**
 * @author muming
 *
 */
@Path("api/log")
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
     * @param parameters
     *            (taskId、offset：日志内容的字节偏移量、lines：日志读取的行数)
     *
     * @return
     * @throws Exception
     */
    @POST
    @Path("executeLog")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult<?> executeLog(@FormParam("appToken") String appToken, @FormParam("appName") String appName, @FormParam("user") String user,
            @FormParam("parameters") String parameters) {

        try {
            AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JSONObject para = new JSONObject(parameters);
            Long taskId = para.getLong("taskId");
            Long offset = para.getLong("offset");
            Integer lines = para.getInt("lines");

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
     * @param parameters
     *            (taskId、offset：日志内容的字节偏移量、lines：日志读取的行数)
     *
     * @return
     * @throws Exception
     */
    @POST
    @Path("result")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult<?> result(@FormParam("appToken") String appToken, @FormParam("appName") String appName, @FormParam("user") String user,
            @FormParam("parameters") String parameters) throws Exception {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JSONObject para = new JSONObject(parameters);
            Long taskId = para.getLong("taskId");
            Long offset = para.getLong("offset");
            Integer lines = para.getInt("lines");
            Integer type = para.getInt("type");

            RestServerReadLogRequest request = RestServerReadLogRequest.newBuilder().setTaskId(taskId).setType(StreamType.STD_OUT.getValue())
                    .setOffset(offset).setType(type).setAppAuth(appAuth).setLines(lines).build();

            LogServerReadLogResponse response = (LogServerReadLogResponse) callActor(AkkaType.LOGSTORAGE, request);

            if (response.getSuccess()) {
                LogVo logVo = new LogVo();
                logVo.setOffset(response.getOffset());
                logVo.setLog(response.getLog());
                logVo.setIsEnd(response.getIsEnd());
                return successResult(logVo);
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return errorResult(e.getMessage());
        }
    }

}
