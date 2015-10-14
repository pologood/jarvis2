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

import com.mogujie.jarvis.protocol.ModifyDependencyProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.ModifyDependencyProtos.DependencyEntry.DependencyOperator;
import com.mogujie.jarvis.protocol.QueryJobRelationProtos.RestServerQueryJobRelationRequest;
import com.mogujie.jarvis.protocol.QueryJobRelationProtos.RestServerQueryJobRelationRequest.RelationType;
import com.mogujie.jarvis.protocol.QueryJobRelationProtos.ServerQueryJobRelationResponse;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.ServerSubmitJobResponse;
import com.mogujie.jarvis.server.JarvisServerActorSystem;
import com.mogujie.jarvis.server.domain.RemoveJobRequest;
import com.mogujie.jarvis.server.scheduler.dag.strategy.CommonStrategy;
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
        new JavaTestKit(system) {{
            Props props = SpringExtension.SPRING_EXT_PROVIDER.get(system).props("jobActor");
            ActorRef actorRef = system.actorOf(props);
            RestServerSubmitJobRequest request = RestServerSubmitJobRequest.newBuilder()
                    .setJobName("testJob1")
                    .setCronExpression("4 1 * * * ?")
                    .setAppName("testApp1")
                    .setAppKey("appKey1")
                    .setUser("testUser1")
                    .setJobType("hive_sql")
                    .setContent("select * from test1")
                    .setGroupId(1)
                    .build();

            Future<Object> future = Patterns.ask(actorRef, request, TIMEOUT);
            long jobId = 0;
            try {
                ServerSubmitJobResponse response = (ServerSubmitJobResponse) Await.result(future, TIMEOUT.duration());
                Assert.assertTrue(response.getSuccess());
                jobId = response.getJobId();
                Assert.assertTrue(jobId > 0);
            } catch (Exception e) {
                Assert.assertTrue(false);
            }

            actorRef.tell(new RemoveJobRequest(jobId), getRef());
            expectMsgEquals("remove success");
        }};
    }

    /**
     *   A   B
     *    \ /
     *     C
     */
    @Test
    public void testSubmitJob2() {
        new JavaTestKit(system) {{
            Props props = SpringExtension.SPRING_EXT_PROVIDER.get(system).props("jobActor");
            ActorRef actorRef = system.actorOf(props);

            // submit jobA
            RestServerSubmitJobRequest request = RestServerSubmitJobRequest.newBuilder()
                    .setJobName("testJob1")
                    .setCronExpression("4 1 * * ?")
                    .setAppName("testApp1")
                    .setAppKey("appKey1")
                    .setUser("testUser")
                    .setJobType("hive_sql")
                    .setContent("select * from test1")
                    .setGroupId(1)
                    .build();

            Future<Object> future = Patterns.ask(actorRef, request, TIMEOUT);
            long jobAId = 0;
            try {
                ServerSubmitJobResponse response = (ServerSubmitJobResponse) Await.result(future, TIMEOUT.duration());
                Assert.assertTrue(response.getSuccess());
                jobAId = response.getJobId();
                Assert.assertTrue(jobAId > 0);
            } catch (Exception e) {
                Assert.assertTrue(false);
            }

            // submit jobB
            request = RestServerSubmitJobRequest.newBuilder()
                    .setJobName("testJob2")
                    .setCronExpression("4 2 * * ?")
                    .setAppName("testApp2")
                    .setAppKey("appKey2")
                    .setUser("testUser")
                    .setJobType("hive_sql")
                    .setContent("select * from test2")
                    .setGroupId(1)
                    .build();

            future = Patterns.ask(actorRef, request, TIMEOUT);
            long jobBId = 0;
            try {
                ServerSubmitJobResponse response = (ServerSubmitJobResponse) Await.result(future, TIMEOUT.duration());
                Assert.assertTrue(response.getSuccess());
                jobBId = response.getJobId();
                Assert.assertTrue(jobBId > 0);
            } catch (Exception e) {
                Assert.assertTrue(false);
            }

            // submit jobC
            DependencyEntry entryA = DependencyEntry.newBuilder()
                    .setJobId(jobAId)
                    .setOperator(DependencyOperator.ADD)
                    .setCommonDependStrategy(CommonStrategy.ALL.getValue())
                    .build();
            DependencyEntry entryB = DependencyEntry.newBuilder()
                    .setJobId(jobBId)
                    .setOperator(DependencyOperator.ADD)
                    .setCommonDependStrategy(CommonStrategy.ALL.getValue())
                    .setOffsetDependStrategy("lastday")
                    .build();

            request = RestServerSubmitJobRequest.newBuilder()
                    .setJobName("testJob3")
                    .addDependencyEntry(entryA)
                    .addDependencyEntry(entryB)
                    .setAppName("testApp3")
                    .setAppKey("appKey3")
                    .setUser("testUser")
                    .setJobType("hive_sql")
                    .setContent("select * from test3")
                    .setGroupId(1)
                    .build();

            future = Patterns.ask(actorRef, request, TIMEOUT);
            long jobCId = 0;
            try {
                ServerSubmitJobResponse response = (ServerSubmitJobResponse) Await.result(future, TIMEOUT.duration());
                Assert.assertTrue(response.getSuccess());
                jobCId = response.getJobId();
                Assert.assertTrue(jobCId > 0);
            } catch (Exception e) {
                Assert.assertTrue(false);
            }

            // get parents of jobC, should return jobA and jobB
            RestServerQueryJobRelationRequest queryParentRequest = RestServerQueryJobRelationRequest.newBuilder()
                    .setJobId(jobCId)
                    .setRelationType(RelationType.PARENTS)
                    .build();
            future = Patterns.ask(actorRef, queryParentRequest, TIMEOUT);
            try {
                ServerQueryJobRelationResponse response = (ServerQueryJobRelationResponse) Await.result(future, TIMEOUT.duration());
                Assert.assertTrue(response.getSuccess());
                Assert.assertEquals(2, response.getJobFlagEntryList().size());
            } catch (Exception e) {
                Assert.assertTrue(false);
            }

            actorRef.tell(new RemoveJobRequest(jobAId), getRef());
            expectMsgEquals("remove success");
            actorRef.tell(new RemoveJobRequest(jobBId), getRef());
            expectMsgEquals("remove success");
            actorRef.tell(new RemoveJobRequest(jobCId), getRef());
            expectMsgEquals("remove success");
        }};
    }

    /**
     *     A
     *    / \
     *   B   C
     */
    @Test
    public void testSubmitJob3() {
        new JavaTestKit(system) {{
            Props props = SpringExtension.SPRING_EXT_PROVIDER.get(system).props("jobActor");
            ActorRef actorRef = system.actorOf(props);

            // submit jobA
            RestServerSubmitJobRequest request = RestServerSubmitJobRequest.newBuilder()
                    .setJobName("testJob1")
                    .setCronExpression("4 1 * * ?")
                    .setAppName("testApp1")
                    .setAppKey("appKey1")
                    .setUser("testUser")
                    .setJobType("hive_sql")
                    .setContent("select * from test1")
                    .setGroupId(1)
                    .build();

            Future<Object> future = Patterns.ask(actorRef, request, TIMEOUT);
            long jobAId = 0;
            try {
                ServerSubmitJobResponse response = (ServerSubmitJobResponse) Await.result(future, TIMEOUT.duration());
                Assert.assertTrue(response.getSuccess());
                jobAId = response.getJobId();
                Assert.assertTrue(jobAId > 0);
            } catch (Exception e) {
                Assert.assertTrue(false);
            }

            // submit jobB
            DependencyEntry entryB = DependencyEntry.newBuilder()
                    .setJobId(jobAId)
                    .setOperator(DependencyOperator.ADD)
                    .setCommonDependStrategy(CommonStrategy.ALL.getValue())
                    .build();
            request = RestServerSubmitJobRequest.newBuilder()
                    .setJobName("testJob2")
                    .addDependencyEntry(entryB)
                    .setAppName("testApp2")
                    .setAppKey("appKey2")
                    .setUser("testUser")
                    .setJobType("hive_sql")
                    .setContent("select * from test2")
                    .setGroupId(1)
                    .build();

            future = Patterns.ask(actorRef, request, TIMEOUT);
            long jobBId = 0;
            try {
                ServerSubmitJobResponse response = (ServerSubmitJobResponse) Await.result(future, TIMEOUT.duration());
                Assert.assertTrue(response.getSuccess());
                jobBId = response.getJobId();
                Assert.assertTrue(jobBId > 0);
            } catch (Exception e) {
                Assert.assertTrue(false);
            }

            // submit jobC
            DependencyEntry entryC = DependencyEntry.newBuilder()
                    .setJobId(jobAId)
                    .setOperator(DependencyOperator.ADD)
                    .setCommonDependStrategy(CommonStrategy.ALL.getValue())
                    .setOffsetDependStrategy("lastday")
                    .build();
            request = RestServerSubmitJobRequest.newBuilder()
                    .setJobName("testJob3")
                    .addDependencyEntry(entryC)
                    .setAppName("testApp3")
                    .setAppKey("appKey3")
                    .setUser("testUser")
                    .setJobType("hive_sql")
                    .setContent("select * from test3")
                    .setGroupId(1)
                    .build();

            future = Patterns.ask(actorRef, request, TIMEOUT);
            long jobCId = 0;
            try {
                ServerSubmitJobResponse response = (ServerSubmitJobResponse) Await.result(future, TIMEOUT.duration());
                Assert.assertTrue(response.getSuccess());
                jobCId = response.getJobId();
                Assert.assertTrue(jobCId > 0);
            } catch (Exception e) {
                Assert.assertTrue(false);
            }

            // get children of jobA, should return jobB and jobC
            RestServerQueryJobRelationRequest queryParentRequest = RestServerQueryJobRelationRequest.newBuilder()
                    .setJobId(jobAId)
                    .setRelationType(RelationType.CHILDREN)
                    .build();
            future = Patterns.ask(actorRef, queryParentRequest, TIMEOUT);
            try {
                ServerQueryJobRelationResponse response = (ServerQueryJobRelationResponse) Await.result(future, TIMEOUT.duration());
                Assert.assertTrue(response.getSuccess());
                Assert.assertEquals(2, response.getJobFlagEntryList().size());
            } catch (Exception e) {
                Assert.assertTrue(false);
            }

            actorRef.tell(new RemoveJobRequest(jobAId), getRef());
            expectMsgEquals("remove success");
            actorRef.tell(new RemoveJobRequest(jobBId), getRef());
            expectMsgEquals("remove success");
            actorRef.tell(new RemoveJobRequest(jobCId), getRef());
            expectMsgEquals("remove success");
        }};
    }
}
