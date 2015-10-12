/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年10月12日 上午11:14:54
 */

package com.mogujie.jarvis.server.actor;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.mogujie.jarvis.protocol.SystemStatusProtos.RestServerUpdateSystemStatusRequest;
import com.mogujie.jarvis.server.TaskDispatcher;

import akka.actor.UntypedActor;

/**
 * 
 *
 */
@Named("systemActor")
@Scope("prototype")
public class SystemActor extends UntypedActor {

    @Autowired
    private TaskDispatcher taskDispatcher;

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestServerUpdateSystemStatusRequest) {
            RestServerUpdateSystemStatusRequest request = (RestServerUpdateSystemStatusRequest) obj;
            if (request.getStatus() > 0) {
                taskDispatcher.restart();
            } else {
                taskDispatcher.pause();
            }
        } else {
            unhandled(obj);
        }
    }

    public static Set<Class<?>> handledMessages() {
        Set<Class<?>> set = new HashSet<>();
        set.add(RestServerUpdateSystemStatusRequest.class);
        return set;
    }

}
