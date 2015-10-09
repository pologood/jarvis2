/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年10月9日 下午5:14:53
 */

package com.mogujie.jarvis.server.actor;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;

import com.mogujie.jarvis.protocol.ModifyAppParallelismProtos.RestServerModifyAppParallelismRequest;
import com.mogujie.jarvis.protocol.ModifyAppParallelismProtos.ServerModifyAppParallelismResponse;
import com.mogujie.jarvis.server.TaskManager;

import akka.actor.UntypedActor;

/**
 * 
 *
 */
@Named("appActor")
public class AppActor extends UntypedActor {

    @Autowired
    private TaskManager taskManager;

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestServerModifyAppParallelismRequest) {
            RestServerModifyAppParallelismRequest request = (RestServerModifyAppParallelismRequest) obj;
            taskManager.updateAppMaxParallelism(request.getAppName(), request.getParallelism());

            ServerModifyAppParallelismResponse response = ServerModifyAppParallelismResponse.newBuilder().setSuccess(true).build();
            getSender().tell(response, getSelf());
        } else {
            unhandled(obj);
        }
    }

    public static Set<Class<?>> handledMessages() {
        Set<Class<?>> set = new HashSet<>();
        set.add(RestServerModifyAppParallelismRequest.class);
        return set;
    }

}
