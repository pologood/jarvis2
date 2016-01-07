package com.mogujie.jarvis.rest.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.core.domain.AppStatus;
import com.mogujie.jarvis.core.domain.OperationMode;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.ApplicationProtos.RestCreateApplicationRequest;
import com.mogujie.jarvis.protocol.ApplicationProtos.RestModifyApplicationRequest;
import com.mogujie.jarvis.protocol.ApplicationProtos.ServerCreateApplicationResponse;
import com.mogujie.jarvis.protocol.ApplicationProtos.ServerModifyApplicationResponse;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.utils.ConvertValidUtils;
import com.mogujie.jarvis.rest.utils.JsonParameters;

/**
 * @author muming
 */
@Path("api/app")
public class AppController extends AbstractController {

    /**
     * 追加app
     */
    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult add(@FormParam("user") String user,
                          @FormParam("appToken") String appToken,
                          @FormParam("appName") String appName,
                          @FormParam("parameters") String parameters) {
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JsonParameters paras = new JsonParameters(parameters);
            String applicationName = paras.getStringNotEmpty("applicationName");
            String owner = paras.getStringNotEmpty("owner");
            Integer status = paras.getInteger("status", AppStatus.ENABLE.getValue());
            Integer maxConcurrency = paras.getInteger("maxConcurrency", 10);

            ConvertValidUtils.checkAppVo(OperationMode.ADD, applicationName, owner, status, maxConcurrency);
            RestCreateApplicationRequest request = RestCreateApplicationRequest.newBuilder().setAppAuth(appAuth).setUser(user)
                    .setAppName(applicationName.trim()).setOwner(owner)
                    .setStatus(status).setMaxConcurrency(maxConcurrency)
                    .build();

            ServerCreateApplicationResponse response = (ServerCreateApplicationResponse) callActor(AkkaType.SERVER, request);
            if (response.getSuccess()) {
                return successResult();
            } else {
                return errorResult(response.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return errorResult(e.getMessage());
        }
    }

    /**
     * 修改app
     */
    @POST
    @Path("edit")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult update(@FormParam("user") String user,
                             @FormParam("appName") String appName,
                             @FormParam("appToken") String appToken,
                             @FormParam("parameters") String parameters) {
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JsonParameters paras = new JsonParameters(parameters);
            Integer appId = paras.getIntegerNotNull("appId");
            String applicationName = paras.getString("applicationName");
            String owner = paras.getString("owner");
            Integer status = paras.getInteger("status");
            Integer maxConcurrency = paras.getInteger("maxConcurrency");

            ConvertValidUtils.checkAppVo(OperationMode.EDIT, applicationName, owner, status, maxConcurrency);
            RestModifyApplicationRequest.Builder builder = RestModifyApplicationRequest.newBuilder();
            builder.setAppAuth(appAuth).setUser(user).setAppId(appId);
            if (applicationName != null) {
                builder.setAppName(applicationName.trim());
            }
            if (owner != null) {
                builder.setOwner(owner.trim());
            }
            if (status != null) {
                builder.setStatus(status);
            }
            if (maxConcurrency != null) {
                builder.setMaxConcurrency(maxConcurrency);
            }
            RestModifyApplicationRequest request = builder.build();

            ServerModifyApplicationResponse response = (ServerModifyApplicationResponse) callActor(AkkaType.SERVER, request);
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
