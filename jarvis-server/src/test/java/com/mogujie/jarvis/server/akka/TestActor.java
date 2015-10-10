/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年10月10日 上午11:14:55
 */

package com.mogujie.jarvis.server.akka;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.testkit.JavaTestKit;

/**
 * @author guangming
 *
 */
public class TestActor {
    static class MyActor extends UntypedActor {
        public void onReceive(Object o) throws Exception {
            if (o.equals("hello")) {
                getSender().tell("world", getSelf());
            } else {
                unhandled(o);
            }
        }
    }

    protected static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("mytest");
    }

    @AfterClass
    public static void tearDown() {
        JavaTestKit.shutdownActorSystem(system);
    }

    @Test
    public void testHellWorld() {
        new JavaTestKit(system) {{
            Props props = Props.create(MyActor.class);
            ActorRef actorRef = system.actorOf(props);
            actorRef.tell("hello", getRef());
            expectMsgEquals("world");
        }};
    }
}
