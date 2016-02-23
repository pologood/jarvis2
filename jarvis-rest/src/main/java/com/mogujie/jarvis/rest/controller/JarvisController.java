/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年2月23日 下午1:17:37
 */

package com.mogujie.jarvis.rest.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.rest.jarvis.Result;
import com.mogujie.jarvis.rest.jarvis.TaskInfo;
import com.mogujie.jarvis.rest.jarvis.TaskInfoResult;
import com.mogujie.jarvis.rest.jarvis.TasksResult;
import com.mogujie.jarvis.rest.jarvis.User;

/**
 * @author guangming
 *
 */
@Deprecated
@Path("api")
public class JarvisController {

    @GET
    @Path("taskinfo")
    @Produces(MediaType.APPLICATION_JSON)
    public TaskInfoResult getTaskInfo(@PathParam("scriptId") int scriptId) {
        //TODO
        return null;
    }

    @GET
    @Path("alltasks")
    @Produces(MediaType.APPLICATION_JSON)
    public TasksResult getAllTasks() {
        //TODO
        return null;
    }

    @GET
    @Path("getdependencybyscript.htm")
    @Produces(MediaType.APPLICATION_JSON)
    public TasksResult getDependencyByScript(@PathParam("scriptId") int scriptId) {
        //TODO
        return null;
    }

    @GET
    @Path("sdependtasks")
    @Produces(MediaType.APPLICATION_JSON)
    public TasksResult getScriptDepend(@PathParam("scriptId") int scriptId) {
        //TODO
        return null;
    }

    @GET
    @Path("searchtask")
    @Produces(MediaType.APPLICATION_JSON)
    public TasksResult searchTask(@PathParam("keyword") String title) {
        //TODO
        return null;
    }

    @POST
    @Path("submittask")
    @Produces(MediaType.APPLICATION_JSON)
    public Result submitTask(@FormParam("task") String task, @FormParam("globalUser") String globalUser,
            User user) {
        TaskInfo taskInfo = JsonHelper.fromJson(task, TaskInfo.class);
        return null;
    }
}
