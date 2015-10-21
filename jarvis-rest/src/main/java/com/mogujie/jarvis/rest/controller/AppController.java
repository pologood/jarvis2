package com.mogujie.jarvis.rest.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.protocol.AppAuthProtos;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.ApplicationProtos.RestServerCreateApplicationRequest;
import com.mogujie.jarvis.protocol.ApplicationProtos.RestServerModifyApplicationRequest;
import com.mogujie.jarvis.protocol.ApplicationProtos.ServerCreateApplicationResponse;
import com.mogujie.jarvis.protocol.ApplicationProtos.ServerModifyApplicationResponse;
import com.mogujie.jarvis.rest.RestResult;
import org.json.JSONObject;

/**
 * Created by hejian on 15/10/15.
 */
@Path("api/app")
public class AppController extends AbstractController {

    /**
    * 新增app
    */
    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult add(@FormParam("user") String user,
                          @FormParam("appToken") String appToken,
                          @FormParam("appName") String appName,
                          @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JSONObject para=new JSONObject(parameters);
            String applicationName=para.getString("applicationName");

            RestServerCreateApplicationRequest request = RestServerCreateApplicationRequest.newBuilder().setAppName(applicationName)
                    .setAppAuth(appAuth).build();

            ServerCreateApplicationResponse response = (ServerCreateApplicationResponse) callActor(AkkaType.SERVER, request);
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

    /**
     * 更新app
     */
    @POST
    @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult update(@FormParam("user") String user,
                             @FormParam("appName") String appName,
                             @FormParam("appToken") String appToken,
                             @FormParam("parameters") String parameters) {
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JSONObject para=new JSONObject(parameters);

            String applicationName=para.getString("applicationName");
            Integer appId=para.getInt("appId");
            Integer status=para.getInt("status");

            RestServerModifyApplicationRequest request = RestServerModifyApplicationRequest.newBuilder().setAppAuth(appAuth)
                    .setAppName(applicationName).setStatus(status).build();
            ServerModifyApplicationResponse response = (ServerModifyApplicationResponse) callActor(AkkaType.SERVER, request);
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

    /**
     * 修改app状态
     */
    @POST
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult delete(@FormParam("user") String user,
                             @FormParam("appName") String appName,
                             @FormParam("appToken") String appToken,
                             @FormParam("parameters") String parameters) {
        try {
            AppAuth appAuth = AppAuth.newBuilder().setName(appName).setToken(appToken).build();

            JSONObject para=new JSONObject(parameters);

            String applicationName=para.getString("applicationName");
            Integer appId=para.getInt("appId");
            Integer status=para.getInt("status");



            RestServerModifyApplicationRequest request = RestServerModifyApplicationRequest.newBuilder()
                    .setAppAuth(appAuth).setStatus(status).build();
            ServerModifyApplicationResponse response = (ServerModifyApplicationResponse) callActor(AkkaType.SERVER, request);
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
