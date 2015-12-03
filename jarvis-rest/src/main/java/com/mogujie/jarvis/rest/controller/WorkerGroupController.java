package com.mogujie.jarvis.rest.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.protocol.AppAuthProtos;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.RestServerCreateWorkerGroupRequest;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.RestServerModifyWorkerGroupRequest;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.ServerCreateWorkerGroupResponse;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.ServerModifyWorkerGroupResponse;
import com.mogujie.jarvis.rest.RestResult;

/**
 * Created by hejian on 15/10/15.
 */
@Path("api/workerGroup")
public class WorkerGroupController extends AbstractController {

    /**
     * 新增worker group
     * 
     * @author hejian
     */
    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult<?> add(@FormParam("appName") String appName, @FormParam("user") String user, @FormParam("appToken") String appToken,
            @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JSONObject para = new JSONObject(parameters);
            String name = para.getString("name");

            RestServerCreateWorkerGroupRequest request = RestServerCreateWorkerGroupRequest.newBuilder().setWorkerGroupName(name).setUser(user)
                    .setAppAuth(appAuth).build();

            ServerCreateWorkerGroupResponse response = (ServerCreateWorkerGroupResponse) callActor(AkkaType.SERVER, request);

            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // logger.error(e.getMessage(),e);
            return errorResult(e.getMessage());
        }
    }

    /**
     * 更新worker group
     * 
     * @author hejian
     */
    @POST
    @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult<?> update(@FormParam("appName") String appName, @FormParam("appToken") String appToken, @FormParam("user") String user,
            @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JSONObject para = new JSONObject(parameters);
            Integer workerGroupId = para.getInt("workerGroupId");
            String name = para.getString("name");

            RestServerModifyWorkerGroupRequest request = RestServerModifyWorkerGroupRequest.newBuilder().setWorkerGroupId(workerGroupId)
                    .setAppAuth(appAuth).setWorkerGroupName(name).setUser(user).build();

            ServerModifyWorkerGroupResponse response = (ServerModifyWorkerGroupResponse) callActor(AkkaType.SERVER, request);
            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // logger.error("", e);
            return errorResult(e.getMessage());
        }
    }

    /**
     * 更新worker group状态
     * 
     * @author hejian
     */
    @POST
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult<?> delete(@FormParam("user") String user, @FormParam("appName") String appName, @FormParam("appToken") String appToken,
            @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JSONObject para = new JSONObject(parameters);
            Integer workerGroupId = para.getInt("workerGroupId");
            Integer status = para.getInt("status");

            RestServerModifyWorkerGroupRequest request = RestServerModifyWorkerGroupRequest.newBuilder().setWorkerGroupId(workerGroupId)
                    .setAppAuth(appAuth).setStatus(status).build();

            ServerModifyWorkerGroupResponse response = (ServerModifyWorkerGroupResponse) callActor(AkkaType.SERVER, request);

            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // logger.error("", e);
            return errorResult(e.getMessage());
        }
    }
}
