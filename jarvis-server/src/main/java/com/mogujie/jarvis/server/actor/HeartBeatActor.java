/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月10日 上午11:21:23
 */

package com.mogujie.jarvis.server.actor;

import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.mogujie.jarvis.protocol.HeartBeatProtos.HeartBeatRequest;
import com.mogujie.jarvis.server.service.HeartBeatService;

import akka.actor.Props;
import akka.actor.UntypedActor;

@Named("heartBeatActor")
@Scope("prototype")
public class HeartBeatActor extends UntypedActor {

    @Autowired
    private HeartBeatService heartBeatService;

    private static final Logger LOGGER = LogManager.getLogger("heartbeat");

    public static Props props() {
        return Props.create(HeartBeatActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof HeartBeatRequest) {

        } else {
            unhandled(obj);
        }
    }

}
