/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午4:11:05
 */

package com.mogujie.jarvis.server.actor;

import java.util.ArrayList;
import java.util.List;

import com.mogujie.jarvis.protocol.ReadLogProtos.RestServerReadLogRequest;
import com.mogujie.jarvis.protocol.WriteLogProtos.WorkerWriteLogRequest;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * @author wuya
 *
 */
public class LogRoutingActor extends UntypedActor {

    private int size;
    private List<ActorRef> writeActors = new ArrayList<ActorRef>();

    public LogRoutingActor(int size) {
        this.size = size;
//        for (int i = 0; i < size; i++) {
//            writeActors.add(getContext().actorOf(LogWriteActor.props()));
//        }
    }

    @Override
    public void onReceive(Object obj) throws Exception {
//        if (obj instanceof WorkerWriteLogRequest) {
//            WorkerWriteLogRequest msg = (WorkerWriteLogRequest) obj;
//            writeActors.get(msg.getFullId().hashCode() % size).tell(msg, getSelf());
//        } else if (obj instanceof RestServerReadLogRequest) {
//            ActorRef ref = getContext().actorOf(LogReadActor.props());
//            ref.forward(obj, getContext());
//        } else {
//            unhandled(obj);
//        }
    }

}
