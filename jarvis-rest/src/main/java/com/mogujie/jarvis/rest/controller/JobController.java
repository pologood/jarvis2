/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: muming
 * Create Date: 2015年10月08日 下午3:19:28
 */
package com.mogujie.jarvis.rest.controller;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.protocol.MapEntryProtos;
import com.mogujie.jarvis.protocol.SubmitJobProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.ServerSubmitJobResponse;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.vo.JobVo;

/**
 * @author muming
 */
@Path("job")
public class JobController extends AbstractController {

    /**
     * 提交job任务
     *
     * @throws Exception
     */
    @POST
    @Path("submitJob")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult onlineClient(@FormParam("appKey") String appKey,
                                   @FormParam("appName") String appName,
                                   @FormParam("user") String user,
                                   @FormParam("jobName") String jobName,
                                   @FormParam("jobType") String jobType,
                                   @FormParam("dependJobIds") String dependJobIds,
                                   @FormParam("cronExp") String cronExp,
                                   @FormParam("jobContent") String jobContent,
                                   @FormParam("groupId") int groupId,
                                   @FormParam("priority") int priority,
                                   @FormParam("startTime") long startTime,
                                   @FormParam("endTime") long endTime,
                                   @FormParam("rejectRetries") int rejectRetries,
                                   @FormParam("rejectInterval") int rejectInterval,
                                   @FormParam("failedRetries") int failedRetries,
                                   @FormParam("failedInterval") int failedInterval,
                                   @FormParam("parameters") String parameters) throws Exception {

        // todo , 转换为 list
        List<DependencyEntry> dependEntryList = null;

        // todo parameters 从json转化为 list
        List<MapEntryProtos.MapEntry> paraList = null;

        RestServerSubmitJobRequest request = RestServerSubmitJobRequest.newBuilder().setAppName(appName).setJobName(jobName)
                .setCronExpression(cronExp).addAllDependencyEntry(dependEntryList).setUser(user).setJobType(jobType).setContent(jobContent)
                .setGroupId(groupId).setPriority(priority).setFailedRetries(failedRetries).setFailedInterval(failedInterval)
                .setRejectRetries(rejectRetries).setRejectInterval(rejectInterval).setStartTime(startTime).setEndTime(endTime)
                .addAllParameters(paraList).build();

        ServerSubmitJobResponse response = (ServerSubmitJobResponse) callActor(AkkaType.server, request);

        if (response.getSuccess()) {

            JobVo vo = new JobVo();
            vo.setJobId(response.getJobId());
            return successResult(vo);
        } else {
            return errorResult(response.getMessage());
        }

    }


    /**
     * 提交job任务
     *
     * @throws Exception
     */
    @POST
    @Path("test")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult test(@FormParam("appKey") String appKey,
                           @FormParam("appName") String appName,
                           @FormParam("user") String user
                           ) throws Exception {


            JobVo vo = new JobVo();
            vo.setJobId(123456);
            return successResult(vo);

    }


}
