/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午4:10:00
 */

package com.mogujie.jarvis.server.actor;

import com.mogujie.jarvis.protocol.ReadLogProtos.RestServerReadLogRequest;

import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * @author wuya
 *
 */
public class LogReadActor extends UntypedActor {

    public static Props props() {
        return Props.create(LogReadActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        // TODO Auto-generated method stub
        if (obj instanceof RestServerReadLogRequest) {

        } else {
            unhandled(obj);
        }
    }

}
