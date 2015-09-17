/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午4:10:33
 */

package com.mogujie.jarvis.logcenter.actor;

import com.mogujie.jarvis.protocol.WriteLogProtos.WorkerWriteLogRequest;

import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * @author wuya
 *
 */
public class LogWriteActor extends UntypedActor {

    public static Props props() {
        return Props.create(LogWriteActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        // TODO Auto-generated method stub
        if (obj instanceof WorkerWriteLogRequest) {

        } else {
            unhandled(obj);
        }
    }

}
