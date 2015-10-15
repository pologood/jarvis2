package com.mogujie.jarvis.rest.controller;

import com.mogujie.jarvis.rest.RestResult;
import org.apache.log4j.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by hejian on 15/10/15.
 */
@Path("worker")
public class WorkerController extends AbstractController  {
    Logger logger = Logger.getLogger(this.getClass());

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult add(@FormParam("appName")String appName,
                          @FormParam("status")String status){
        try {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("", e);
            return errorResult(e.getMessage());
        }
    }

    @POST
    @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult update(@FormParam("appId")String appId,
                             @FormParam("appName")String appName,
                             @FormParam("status")String status){
        try {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("", e);
            return errorResult(e.getMessage());
        }
    }

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResult delete(@FormParam("appId")String appId){
        try {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("", e);
            return errorResult(e.getMessage());
        }
    }
}
