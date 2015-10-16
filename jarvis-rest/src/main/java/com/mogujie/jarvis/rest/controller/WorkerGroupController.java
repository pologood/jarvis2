package com.mogujie.jarvis.rest.controller;

import com.google.protobuf.GeneratedMessage;
import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.protocol.AppAuthProtos;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos;
import com.mogujie.jarvis.protocol.WorkerGroupProtos;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.vo.WorkerGroupVo;
import org.apache.log4j.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.*;
import com.mogujie.jarvis.protocol.AppAuthProtos.*;

/**
 * Created by hejian on 15/10/15.
 */
@Path("workerGroup")
public class WorkerGroupController extends AbstractController {
    Logger logger = Logger.getLogger(this.getClass());

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult add(@FormParam("name")String name,
                          @FormParam("user")String user,
                          @FormParam("appName")String appName,
                          @FormParam("appKey")String appKey){
        try {
            AppAuthProtos.AppAuth appAuth= AppAuthProtos.AppAuth.newBuilder().setName(appName).setKey(appKey).build();

            RestServerCreateWorkerGroupRequest request=RestServerCreateWorkerGroupRequest.newBuilder()
                    .setWorkerGroupName(name).setUser(user).setAppAuth(appAuth).build();

            ServerCreateWorkerGroupResponse response=(ServerCreateWorkerGroupResponse) callActor(AkkaType.SERVER,request);

            if(response.getSuccess()){
                return successResult();
            }
            else{
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            //logger.error(e.getMessage(),e);
            return errorResult(e.getMessage());
        }
    }

    @POST
    @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult update(@FormParam("workerGroupId")Integer workerGroupId,
                             @FormParam("name")String name,
                             @FormParam("user")String user,
                             @FormParam("appName")String appName,
                             @FormParam("appKey")String appKey){
        try {
            AppAuthProtos.AppAuth appAuth= AppAuthProtos.AppAuth.newBuilder().setName(appName).setKey(appKey).build();


            RestServerModifyWorkerGroupRequest request=RestServerModifyWorkerGroupRequest.newBuilder()
                    .setWorkerGroupId(workerGroupId).setAppAuth(appAuth).setWorkerGroupName(name)
                    .setUser(user).build();

            ServerModifyWorkerGroupResponse response=(ServerModifyWorkerGroupResponse)callActor(AkkaType.SERVER,request);
            if(response.getSuccess()){
                return successResult();
            }
            else{
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            //logger.error("", e);
            return errorResult(e.getMessage());
        }
    }

    @POST
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult delete(@FormParam("workerGroupId")Integer workerGroupId,
                             @FormParam("status")Integer status,
                             @FormParam("user")String user,
                             @FormParam("appName")String appName,
                             @FormParam("appKey")String appKey){
        try {
            AppAuthProtos.AppAuth appAuth= AppAuthProtos.AppAuth.newBuilder().setName(appName).setKey(appKey).build();


            RestServerModifyWorkerGroupRequest request=RestServerModifyWorkerGroupRequest.newBuilder()
                    .setWorkerGroupId(workerGroupId).setAppAuth(appAuth).setStatus(status).build();

            ServerModifyWorkerGroupResponse response=(ServerModifyWorkerGroupResponse)callActor(AkkaType.SERVER,request);


            if(response.getSuccess()){
                return successResult();
            }
            else{
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            //logger.error("", e);
            return errorResult(e.getMessage());
        }
    }
}
