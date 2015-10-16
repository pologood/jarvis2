package com.mogujie.jarvis.rest.controller;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.protocol.AppAuthProtos;
import com.mogujie.jarvis.protocol.ApplicationProtos;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos;
import com.mogujie.jarvis.rest.RestResult;
import org.apache.log4j.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.*;
import com.mogujie.jarvis.protocol.AppAuthProtos.*;
import org.apache.logging.log4j.LogManager;

/**
 * Created by hejian on 15/10/15.
 */
@Path("worker")
public class WorkerController extends AbstractController  {
    org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult add(@FormParam("appName")String appName,
                          @FormParam("status")String status){
        try {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("", e);
            return errorResult(e.getMessage());
        }
    }

    @POST
    @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult update(@FormParam("appId")String appId,
                             @FormParam("appName")String appName,
                             @FormParam("status")String status){
        try {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("", e);
            return errorResult(e.getMessage());
        }
    }

    @POST
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult delete(@FormParam("workerId")String workerId,
                             @FormParam("ip")String ip,
                             @FormParam("appName")String appName,
                             @FormParam("appKey")String appKey,
                             @FormParam("port")Integer port,
                             @FormParam("status")Integer status){
        try {
            AppAuth appAuth=AppAuth.newBuilder().setName(appName).setKey(appKey).build();

            RestServerModifyWorkerStatusRequest request = RestServerModifyWorkerStatusRequest.newBuilder()
                    .setIp(ip).setPort(port).setStatus(status).setAppAuth(appAuth).build();

            ServerModifyWorkerStatusResponse response=(ServerModifyWorkerStatusResponse) callActor(AkkaType.SERVER,request);
            if(response.getSuccess()){
                return successResult();
            }
            else{
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("", e);
            return errorResult(e.getMessage());
        }
    }
}
