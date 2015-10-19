package com.mogujie.jarvis.rest.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.RestServerModifyWorkerStatusRequest;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.ServerModifyWorkerStatusResponse;
import com.mogujie.jarvis.rest.MsgCode;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.utils.RequestUtils;
import org.json.JSONObject;

/**
 * Created by hejian on 15/10/15.
 */
@Path("worker")
public class WorkerController extends AbstractController {

    @POST
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult delete(@FormParam("appKey") String appKey,
                             @FormParam("appName") String appName,
                             @FormParam("appToken") String appToken,
                             @FormParam("user") String user,
                             @FormParam("parameters") String parameters) {
        try {
            JSONObject para=new JSONObject(parameters);

            Integer workerId=para.getInt("workerId");
            String ip=para.getString("ip");
            Integer port=para.getInt("port");
            Integer status=para.getInt("status");

            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            RestServerModifyWorkerStatusRequest request = RestServerModifyWorkerStatusRequest.newBuilder().setIp(ip).setPort(port).setStatus(status)
                    .setAppAuth(appAuth).build();

            ServerModifyWorkerStatusResponse response = (ServerModifyWorkerStatusResponse) callActor(AkkaType.SERVER, request);
            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("", e);
            return errorResult(e.getMessage());
        }
    }
}
