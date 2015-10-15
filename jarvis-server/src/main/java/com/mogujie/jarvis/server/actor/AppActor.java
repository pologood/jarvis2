/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年10月9日 下午5:14:53
 */

package com.mogujie.jarvis.server.actor;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.mogujie.jarvis.dao.AppMapper;
import com.mogujie.jarvis.dto.App;
import com.mogujie.jarvis.dto.AppExample;
import com.mogujie.jarvis.protocol.ApplicationProtos.RestServerCreateApplicationRequest;
import com.mogujie.jarvis.protocol.ApplicationProtos.RestServerModifyApplicationRequest;
import com.mogujie.jarvis.protocol.ApplicationProtos.ServerCreateApplicationResponse;
import com.mogujie.jarvis.protocol.ApplicationProtos.ServerModifyApplicationResponse;
import com.mogujie.jarvis.server.TaskManager;

import akka.actor.UntypedActor;

/**
 * 
 *
 */
@Named("appActor")
@Scope("prototype")
public class AppActor extends UntypedActor {

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private AppMapper appMapper;

    private Integer queryAppId(String appName) {
        AppExample example = new AppExample();
        example.createCriteria().andAppNameEqualTo(appName);
        List<App> list = appMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return list.get(0).getAppId();
        }

        return null;
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestServerCreateApplicationRequest) {
            RestServerCreateApplicationRequest request = (RestServerCreateApplicationRequest) obj;
            Date date = new Date();
            String key = UUID.randomUUID().toString().replace("-", "");
            App app = new App();
            app.setAppKey(key);
            app.setAppName(request.getAppName());
            app.setStatus(request.getStatus());
            app.setCreateTime(date);
            app.setUpdateTime(date);
            app.setUpdateUser(request.getUser());
            app.setMaxConcurrency(request.getMaxConcurrency());

            appMapper.insertSelective(app);

            ServerCreateApplicationResponse response = ServerCreateApplicationResponse.newBuilder().setSuccess(true).setAppKey(key).build();
            getSender().tell(response, getSelf());
        } else if (obj instanceof RestServerModifyApplicationRequest) {
            RestServerModifyApplicationRequest request = (RestServerModifyApplicationRequest) obj;
            Integer appId = queryAppId(request.getAppAuth().getName());
            App app = new App();
            app.setAppId(appId);
            if (request.hasAppName()) {
                app.setAppName(request.getAppName());
            }

            if (request.hasStatus()) {
                app.setStatus(request.getStatus());
            }

            if (request.hasMaxConcurrency()) {
                app.setMaxConcurrency(request.getMaxConcurrency());
                taskManager.updateAppMaxParallelism(appId, request.getMaxConcurrency());
            }
            appMapper.updateByPrimaryKey(app);

            ServerModifyApplicationResponse response = ServerModifyApplicationResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } else {
            unhandled(obj);
        }
    }

    public static Set<Class<?>> handledMessages() {
        Set<Class<?>> set = new HashSet<>();
        set.add(RestServerCreateApplicationRequest.class);
        set.add(RestServerModifyApplicationRequest.class);
        return set;
    }

}
