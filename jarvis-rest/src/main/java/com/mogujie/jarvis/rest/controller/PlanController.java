package com.mogujie.jarvis.rest.controller;


import com.mogujie.jarvis.protocol.AppAuthProtos;
import com.mogujie.jarvis.rest.RestResult;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by hejian on 15/10/21.
 */
@Path("api/plan")
public class PlanController extends AbstractController {
    @POST
    @Path("dependencies")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult dependencies(@FormParam("appToken") String appToken,
                                 @FormParam("appName") String appName,
                                 @FormParam("user") String user,
                                 @FormParam("parameters") String parameters){
        try {
            AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName(appName).setToken(appToken).build();


            if (true) {
                return successResult();
            } else {
                return errorResult("msg");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return errorResult(e.getMessage());
        }
    }


}
