/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年10月10日 上午9:58:29
 */
package com.mogujie.jarvis.server.akka;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.JavaTestKit;

import com.mogujie.jarvis.server.actor.JobActor;

/**
 * @author guangming
 *
 */
public class TestJobActor extends TestActor {
    @Test
    public void testSubmitJob() {
        new JavaTestKit(system) {{
            Props props = Props.create(JobActor.class);
            ActorRef actorRef = system.actorOf(props);
            // TODO
        }};
    }
}
