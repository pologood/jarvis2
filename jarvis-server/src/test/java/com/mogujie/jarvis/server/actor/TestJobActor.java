/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年10月10日 上午9:58:29
 */
package com.mogujie.jarvis.server.actor;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.testkit.JavaTestKit;
import akka.util.Timeout;

import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.ServerSubmitJobResponse;
import com.mogujie.jarvis.server.JarvisServerActorSystem;
import com.mogujie.jarvis.server.util.SpringContext;
import com.mogujie.jarvis.server.util.SpringExtension;

/**
 * @author guangming
 *
 */
public class TestJobActor {
    private static ActorSystem system;

    private static final Timeout TIMEOUT = new Timeout(Duration.create(5, TimeUnit.SECONDS));

    @BeforeClass
    public static void setup() {
        ApplicationContext context = SpringContext.getApplicationContext();
        system = JarvisServerActorSystem.getInstance();
        SpringExtension.SPRING_EXT_PROVIDER.get(system).initialize(context);
    }

    @AfterClass
    public static void tearDown() {
        JavaTestKit.shutdownActorSystem(system);
    }

    @Test
    public void testSubmitJob1() {
        Props props = SpringExtension.SPRING_EXT_PROVIDER.get(system).props("jobActor");
        ActorRef actorRef = system.actorOf(props);
        RestServerSubmitJobRequest request = RestServerSubmitJobRequest.newBuilder()
                .setJobName("testJob1")
                .setCronExpression("4 1 * * ?")
                .setAppName("testApp1")
                .setAppKey("appKey1")
                .setUser("testUser1")
                .setJobType("hive_sql")
                .setContent("select * from test1")
                .setGroupId(1)
                .build();

        Future<Object> future = Patterns.ask(actorRef, request, TIMEOUT);
        try {
            ServerSubmitJobResponse response = (ServerSubmitJobResponse) Await.result(future, TIMEOUT.duration());
            Assert.assertTrue(response.getSuccess());
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }
}
