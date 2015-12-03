package com.mogujie.jarvis.rest.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.RestServerModifyWorkerStatusRequest;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.ServerModifyWorkerStatusResponse;
import com.mogujie.jarvis.rest.RestResult;

/**
 * Created by hejian on 15/10/15.
 */
@Path("api/worker")
public class WorkerController extends AbstractController {

    /**
     * 修改worker状态
     * 
     * @author hejian
     */
    @POST
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult<?> delete(@FormParam("appName") String appName, @FormParam("appToken") String appToken, @FormParam("user") String user,
            @FormParam("parameters") String parameters) {
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();
            JSONObject para = new JSONObject(parameters);
            String ip = para.getString("ip");
            int port = para.getInt("port");
            int status = para.getInt("status");

            RestServerModifyWorkerStatusRequest request = RestServerModifyWorkerStatusRequest.newBuilder().setIp(ip).setPort(port).setStatus(status)
                    .setAppAuth(appAuth).build();

            ServerModifyWorkerStatusResponse response = (ServerModifyWorkerStatusResponse) callActor(AkkaType.SERVER, request);
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
