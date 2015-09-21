/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月21日 下午4:13:54
 */

package com.mogujie.jarvis.server.actor;

import com.mogujie.jarvis.server.util.SpringExtension;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.SmallestMailboxPool;

/**
 * 
 *
 */
public class ServerActor extends UntypedActor {

    private ActorRef taskMetricsActor = getContext()
            .actorOf(SpringExtension.SPRING_EXT_PROVIDER.get(getContext().system()).props("taskMetricsActor"));
    private ActorRef heartBeatActor = getContext().actorOf(SpringExtension.SPRING_EXT_PROVIDER.get(getContext().system()).props("heartBeatActor"));

    private ActorRef jobSchedulerActor = getContext()
            .actorOf(SpringExtension.SPRING_EXT_PROVIDER.get(getContext().system()).props("ServerActor").withRouter(new SmallestMailboxPool(10)));

    public static Props props() {
        return Props.create(ServerActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {

    }

}
