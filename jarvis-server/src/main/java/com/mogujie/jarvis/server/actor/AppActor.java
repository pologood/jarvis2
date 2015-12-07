/*
 * 蘑菇街 Inc. Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya Create Date: 2015年10月9日 下午5:14:53
 */

package com.mogujie.jarvis.server.actor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.mogujie.jarvis.core.domain.ActorEntry;
import com.mogujie.jarvis.core.domain.MessageType;
import com.mogujie.jarvis.dao.generate.AppMapper;
import com.mogujie.jarvis.dto.generate.App;
import com.mogujie.jarvis.protocol.ApplicationProtos.RestCreateApplicationRequest;
import com.mogujie.jarvis.protocol.ApplicationProtos.RestModifyApplicationRequest;
import com.mogujie.jarvis.protocol.ApplicationProtos.ServerCreateApplicationResponse;
import com.mogujie.jarvis.protocol.ApplicationProtos.ServerModifyApplicationResponse;
import com.mogujie.jarvis.server.TaskManager;

import akka.actor.UntypedActor;

@Named("appActor")
@Scope("prototype")
public class AppActor extends UntypedActor {

    @Autowired
    private TaskManager taskManager;
    @Autowired
    private AppMapper appMapper;

    public static List<ActorEntry> handledMessages() {
        List<ActorEntry> list = new ArrayList<>();
        list.add(new ActorEntry(RestCreateApplicationRequest.class, ServerCreateApplicationResponse.class, MessageType.SYSTEM));
        list.add(new ActorEntry(RestModifyApplicationRequest.class, ServerModifyApplicationResponse.class, MessageType.SYSTEM));
        return list;
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestCreateApplicationRequest) {
            createApplication((RestCreateApplicationRequest) obj);
        } else if (obj instanceof RestModifyApplicationRequest) {
            modifyApplication((RestModifyApplicationRequest) obj);
        } else {
            unhandled(obj);
        }
    }

    public void createApplication(RestCreateApplicationRequest request) {
        ServerCreateApplicationResponse response = null;
        try {
            String key = UUID.randomUUID().toString().replace("-", "");
            Date date = new Date();
            App app = new App();
            app.setAppName(request.getAppName());
            app.setAppKey(key);
            app.setStatus(request.getStatus());
            app.setMaxConcurrency(request.getMaxConcurrency());
            app.setCreateTime(date);
            app.setUpdateTime(date);
            app.setUpdateUser(request.getUser());
            appMapper.insertSelective(app);
            taskManager.addApp(app.getAppId(), request.getMaxConcurrency());
            response = ServerCreateApplicationResponse.newBuilder().setSuccess(true).build();
        } catch (Exception ex) {
            response = ServerCreateApplicationResponse.newBuilder().setSuccess(false).setMessage(ex.getMessage()).build();
        } finally {
            getSender().tell(response, getSelf());
        }
    }

    public void modifyApplication(RestModifyApplicationRequest request) {
        ServerModifyApplicationResponse response = null;
        try {
            Date date = new Date();
            App app = new App();
            Integer appId = request.getAppId();
            app.setAppId(appId);
            if (request.hasAppName()) {
                app.setAppName(request.getAppName());
            }
            if (request.hasStatus()) {
                app.setStatus(request.getStatus());
            }
            if (request.hasMaxConcurrency()) {
                app.setMaxConcurrency(request.getMaxConcurrency());
            }
            app.setCreateTime(date);
            app.setUpdateTime(date);
            app.setUpdateUser(request.getUser());

            appMapper.updateByPrimaryKeySelective(app);
            if (request.hasMaxConcurrency()) {
                taskManager.updateAppMaxParallelism(appId, request.getMaxConcurrency());
            }
            response = ServerModifyApplicationResponse.newBuilder().setSuccess(true).build();
        } catch (Exception ex) {
            response = ServerModifyApplicationResponse.newBuilder().setSuccess(false).setMessage(ex.getMessage()).build();
        } finally {
            getSender().tell(response, getSelf());
        }
    }

}
