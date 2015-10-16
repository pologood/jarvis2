package com.mogujie.jarvis.rest.controller;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.protocol.AppAuthProtos;
import com.mogujie.jarvis.rest.RestResult;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.mogujie.jarvis.protocol.ApplicationProtos.*;
import com.mogujie.jarvis.protocol.AppAuthProtos.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by hejian on 15/10/15.
 */
@Path("app")
public class AppController extends AbstractController {
    Logger LOGGER = LogManager.getLogger();

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult add(@FormParam("appName")String appName,
                          @FormParam("appKey")String appKey,
                          @FormParam("applicationName")String applicationName){
        try {
            AppAuthProtos.AppAuth appAuth= AppAuthProtos.AppAuth.newBuilder().setName(appName).setKey(appKey).build();

            RestServerCreateApplicationRequest request=RestServerCreateApplicationRequest.newBuilder()
                                                        .setAppName(applicationName).setAppAuth(appAuth).build();

            ServerCreateApplicationResponse response=(ServerCreateApplicationResponse)callActor(AkkaType.SERVER,request);
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

    @POST
    @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult update(@FormParam("appId")String appId,
                             @FormParam("appName")String appName,
                             @FormParam("appKey")String appKey,
                             @FormParam("applicationName")String applicationName,
                             @FormParam("status")int status){
        try {
            AppAuthProtos.AppAuth appAuth= AppAuthProtos.AppAuth.newBuilder().setName(appName).setKey(appKey).build();
            RestServerModifyApplicationRequest request=RestServerModifyApplicationRequest.newBuilder()
                                                        .setAppAuth(appAuth).setAppName(applicationName).build();
            ServerModifyApplicationResponse response=(ServerModifyApplicationResponse)callActor(AkkaType.SERVER,request);
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

    @POST
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult delete(@FormParam("appId")String appId,
                             @FormParam("appName")String appName,
                             @FormParam("appKey")String appKey,
                             @FormParam("status")int status){
        try {
            AppAuth appAuth= AppAuth.newBuilder().setName(appName).setKey(appKey).build();

            RestServerModifyApplicationRequest request=RestServerModifyApplicationRequest.newBuilder().setAppAuth(appAuth)
                                                        .setStatus(status).build();
            ServerModifyApplicationResponse response=(ServerModifyApplicationResponse)callActor(AkkaType.SERVER,request);
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
