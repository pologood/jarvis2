package com.mogujie.jarvis.rest.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

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
@Path("workerGroup")
public class WorkerGroupController extends AbstractController {
    Logger logger = Logger.getLogger(this.getClass());

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult add(@FormParam("name") String name, @FormParam("user") String user, @FormParam("appName") String appName,
            @FormParam("appToken") String appToken) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

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

    @POST
    @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult update(@FormParam("workerGroupId") Integer workerGroupId, @FormParam("name") String name, @FormParam("user") String user,
            @FormParam("appName") String appName, @FormParam("appToken") String appToken) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

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

    @POST
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult delete(@FormParam("workerGroupId") Integer workerGroupId, @FormParam("status") Integer status, @FormParam("user") String user,
            @FormParam("appName") String appName, @FormParam("appToken") String appToken) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

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
