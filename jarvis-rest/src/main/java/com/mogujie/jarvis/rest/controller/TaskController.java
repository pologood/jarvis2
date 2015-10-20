package com.mogujie.jarvis.rest.controller;

import com.mogujie.jarvis.rest.RestResult;
import org.json.JSONObject;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by hejian on 15/10/19.
 */
@Path("task")
public class TaskController extends AbstractController {


    @POST
    @Path("taskList")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult taskList(@FormParam("user") String user,
                          @FormParam("appToken") String appToken,
                          @FormParam("appName") String appName,
                          @FormParam("parameters") String parameters){

        try {
            JSONObject para=new JSONObject(parameters);
            Long jobId=para.getLong("jobId");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @POST
    @Path("status")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult status(@FormParam("user") String user,
                             @FormParam("appToken") String appToken,
                             @FormParam("appName") String appName,
                             @FormParam("parameters") String parameters) {
        try {
            JSONObject para=new JSONObject(parameters);
            Long taskId=para.getLong("taskId");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @POST
    @Path("kill")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult kill(@FormParam("user") String user,
                           @FormParam("appToken") String appToken,
                           @FormParam("appName") String appName,
                           @FormParam("parameters") String parameters) {
        try {
            JSONObject para=new JSONObject(parameters);
            Long taskId=para.getLong("taskId");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
