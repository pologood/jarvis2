package com.mogujie.jarvis.rest.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.protocol.AppAuthProtos;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.RestServerCreateWorkerGroupRequest;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.RestServerModifyWorkerGroupRequest;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.ServerCreateWorkerGroupResponse;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.ServerModifyWorkerGroupResponse;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.utils.JsonParameters;

/**
 * @author muming,hejian
 */
@Path("api/workerGroup")
public class WorkerGroupController extends AbstractController {

    /**
     * 新增worker group
     */
    @POST

    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult add(@FormParam("appName") String appName, @FormParam("user") String user, @FormParam("appToken") String appToken,
                          @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();
            JsonParameters para = new JsonParameters(parameters);
            String name = para.getStringNotEmpty("name");

            RestServerCreateWorkerGroupRequest request = RestServerCreateWorkerGroupRequest.newBuilder()
                    .setAppAuth(appAuth)
                    .setUser(user)
                    .setWorkerGroupName(name).build();

            ServerCreateWorkerGroupResponse response = (ServerCreateWorkerGroupResponse) callActor(AkkaType.SERVER, request);

            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            return errorResult(e.getMessage());
        }
    }

    /**
     * 更新worker group
     */
    @POST
    @Path("edit")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult edit(@FormParam("appName") String appName, @FormParam("appToken") String appToken, @FormParam("user") String user,
                           @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();
            JsonParameters para = new JsonParameters(parameters);
            int workerGroupId = para.getIntegerNotNull("workerGroupId");
            String name = para.getStringNotEmpty("name");

            RestServerModifyWorkerGroupRequest request = RestServerModifyWorkerGroupRequest.newBuilder()
                    .setAppAuth(appAuth)
                    .setUser(user)
                    .setWorkerGroupName(name)
                    .setWorkerGroupId(workerGroupId).build();

            ServerModifyWorkerGroupResponse response = (ServerModifyWorkerGroupResponse) callActor(AkkaType.SERVER, request);
            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            return errorResult(e.getMessage());
        }
    }

    /**
     * 更新worker group状态
     */
    @POST
    @Path("status/set")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult setStatus(@FormParam("user") String user, @FormParam("appName") String appName, @FormParam("appToken") String appToken,
                                @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JsonParameters para = new JsonParameters(parameters);
            int workerGroupId = para.getIntegerNotNull("workerGroupId");
            int status = para.getIntegerNotNull("status");

            RestServerModifyWorkerGroupRequest request = RestServerModifyWorkerGroupRequest.newBuilder()
                    .setAppAuth(appAuth)
                    .setUser(user)
                    .setWorkerGroupId(workerGroupId)
                    .setStatus(status)
                    .build();

            ServerModifyWorkerGroupResponse response = (ServerModifyWorkerGroupResponse) callActor(AkkaType.SERVER, request);

            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            return errorResult(e.getMessage());
        }
    }
}
