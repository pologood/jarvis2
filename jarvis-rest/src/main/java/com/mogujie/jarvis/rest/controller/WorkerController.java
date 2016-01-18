package com.mogujie.jarvis.rest.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mogujie.jarvis.core.domain.WorkerStatus;
import com.mogujie.jarvis.rest.utils.JsonParameters;
import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.RestServerModifyWorkerStatusRequest;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos.ServerModifyWorkerStatusResponse;
import com.mogujie.jarvis.rest.RestResult;

/**
 * @author muming, hejian
 */
@Path("api/worker")
public class WorkerController extends AbstractController {

    /**
     * 设置worker状态
     */
    @POST
    @Path("status/set")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult statusSet(
            @FormParam("appName") String appName
            , @FormParam("appToken") String appToken
            , @FormParam("user") String user
            , @FormParam("parameters") String parameters) {
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();
            JsonParameters para = new JsonParameters(parameters);
            String ip = para.getStringNotEmpty("ip");
            int port = para.getIntegerNotNull("port");
            int status = para.getIntegerNotNull("status");
            if (!WorkerStatus.isValid(status)) {
                throw new IllegalArgumentException("status值不合法。value:" + status);
            }

            RestServerModifyWorkerStatusRequest request = RestServerModifyWorkerStatusRequest.newBuilder()
                    .setAppAuth(appAuth)
                    .setIp(ip)
                    .setPort(port)
                    .setStatus(status).build();

            ServerModifyWorkerStatusResponse response = (ServerModifyWorkerStatusResponse) callActor(AkkaType.SERVER, request);
            return response.getSuccess() ? successResult() : errorResult(response.getMessage());
        } catch (Exception e) {
            LOGGER.error("", e);
            return errorResult(e);
        }
    }
}
