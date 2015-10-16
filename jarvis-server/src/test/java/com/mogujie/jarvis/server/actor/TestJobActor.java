/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年10月10日 上午9:58:29
 */
package com.mogujie.jarvis.server.actor;

import org.junit.Assert;
import org.junit.Test;

import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.DependencyEntryProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.DependencyEntryProtos.DependencyEntry.DependencyOperator;
import com.mogujie.jarvis.protocol.ModifyJobProtos.RestServerModifyJobRequest;
import com.mogujie.jarvis.protocol.ModifyJobProtos.ServerModifyJobResponse;
import com.mogujie.jarvis.protocol.QueryJobRelationProtos.RestServerQueryJobRelationRequest;
import com.mogujie.jarvis.protocol.QueryJobRelationProtos.RestServerQueryJobRelationRequest.RelationType;
import com.mogujie.jarvis.protocol.QueryJobRelationProtos.ServerQueryJobRelationResponse;
import com.mogujie.jarvis.protocol.SubmitJobProtos.RestServerSubmitJobRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.ServerSubmitJobResponse;
import com.mogujie.jarvis.server.domain.RemoveJobRequest;
import com.mogujie.jarvis.server.scheduler.dag.strategy.CommonStrategy;
import com.mogujie.jarvis.server.util.SpringExtension;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.testkit.JavaTestKit;
import scala.concurrent.Await;
import scala.concurrent.Future;

/**
 * @author guangming
 *
 */
public class TestJobActor extends TestBaseActor {

    private AppAuth appAuth = AppAuth.newBuilder().setName("testApp1").setToken("").build();

    @Test
    public void testSubmitJob1() {
        new JavaTestKit(system) {
            {
                Props props = SpringExtension.SPRING_EXT_PROVIDER.get(system).props("jobActor");
                ActorRef actorRef = system.actorOf(props);

                RestServerSubmitJobRequest request = RestServerSubmitJobRequest.newBuilder().setJobName("testJob1").setCronExpression("0 0 1 * * ?")
                        .setAppAuth(appAuth).setUser("testUser1").setJobType("hive_sql").setContent("select * from test1").setGroupId(1).build();

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
            }
        };
    }

    /**
     * A B \ / C
     */
    @Test
    public void testSubmitJob2() {
        new JavaTestKit(system) {
            {
                Props props = SpringExtension.SPRING_EXT_PROVIDER.get(system).props("jobActor");
                ActorRef actorRef = system.actorOf(props);

                // submit jobA
                RestServerSubmitJobRequest request = RestServerSubmitJobRequest.newBuilder().setJobName("testJob1").setCronExpression("0 0 1 * * ?")
                        .setAppAuth(appAuth).setUser("testUser").setJobType("hive_sql").setContent("select * from test1").setGroupId(1).build();

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
                request = RestServerSubmitJobRequest.newBuilder().setJobName("testJob2").setCronExpression("0 0 2 * * ?").setAppAuth(appAuth)
                        .setUser("testUser").setJobType("hive_sql").setContent("select * from test2").setGroupId(1).build();

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
                DependencyEntry entryA = DependencyEntry.newBuilder().setJobId(jobAId).setOperator(DependencyOperator.ADD)
                        .setCommonDependStrategy(CommonStrategy.ALL.getValue()).build();
                DependencyEntry entryB = DependencyEntry.newBuilder().setJobId(jobBId).setOperator(DependencyOperator.ADD)
                        .setCommonDependStrategy(CommonStrategy.ALL.getValue()).setOffsetDependStrategy("lastday").build();

                request = RestServerSubmitJobRequest.newBuilder().setJobName("testJob3").addDependencyEntry(entryA).addDependencyEntry(entryB)
                        .setAppAuth(appAuth).setUser("testUser").setJobType("hive_sql").setContent("select * from test3").setGroupId(1).build();

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
                RestServerQueryJobRelationRequest queryParentRequest = RestServerQueryJobRelationRequest.newBuilder().setJobId(jobCId)
                        .setRelationType(RelationType.PARENTS).build();
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
            }
        };
    }

    /**
     * A / \ B C
     */
    @Test
    public void testSubmitJob3() {
        new JavaTestKit(system) {
            {
                Props props = SpringExtension.SPRING_EXT_PROVIDER.get(system).props("jobActor");
                ActorRef actorRef = system.actorOf(props);

                // submit jobA
                RestServerSubmitJobRequest request = RestServerSubmitJobRequest.newBuilder().setJobName("testJob1").setCronExpression("0 0 1 * * ?")
                        .setAppAuth(appAuth).setUser("testUser").setJobType("hive_sql").setContent("select * from test1").setGroupId(1).build();

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
                DependencyEntry entryB = DependencyEntry.newBuilder().setJobId(jobAId).setOperator(DependencyOperator.ADD)
                        .setCommonDependStrategy(CommonStrategy.ALL.getValue()).build();
                request = RestServerSubmitJobRequest.newBuilder().setJobName("testJob2").addDependencyEntry(entryB).setAppAuth(appAuth)
                        .setUser("testUser").setJobType("hive_sql").setContent("select * from test2").setGroupId(1).build();

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
                DependencyEntry entryC = DependencyEntry.newBuilder().setJobId(jobAId).setOperator(DependencyOperator.ADD)
                        .setCommonDependStrategy(CommonStrategy.ALL.getValue()).setOffsetDependStrategy("lastday").build();
                request = RestServerSubmitJobRequest.newBuilder().setJobName("testJob3").addDependencyEntry(entryC).setAppAuth(appAuth)
                        .setUser("testUser").setJobType("hive_sql").setContent("select * from test3").setGroupId(1).build();

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
                RestServerQueryJobRelationRequest queryParentRequest = RestServerQueryJobRelationRequest.newBuilder().setJobId(jobAId)
                        .setRelationType(RelationType.CHILDREN).build();
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
            }
        };
    }

    @Test
    public void testModifyJob() {
        new JavaTestKit(system) {
            {
                Props props = SpringExtension.SPRING_EXT_PROVIDER.get(system).props("jobActor");
                ActorRef actorRef = system.actorOf(props);
                RestServerSubmitJobRequest submitJobRequest = RestServerSubmitJobRequest.newBuilder().setJobName("testJob1")
                        .setCronExpression("0 0 1 * * ?").setAppAuth(appAuth).setUser("testUser1").setJobType("hive_sql")
                        .setContent("select * from test1").setGroupId(1).build();

                Future<Object> future = Patterns.ask(actorRef, submitJobRequest, TIMEOUT);
                long jobId = 0;
                try {
                    ServerSubmitJobResponse response = (ServerSubmitJobResponse) Await.result(future, TIMEOUT.duration());
                    Assert.assertTrue(response.getSuccess());
                    jobId = response.getJobId();
                    Assert.assertTrue(jobId > 0);
                } catch (Exception e) {
                    Assert.assertTrue(false);
                }

                RestServerModifyJobRequest modifyJobRequest = RestServerModifyJobRequest.newBuilder().setJobId(jobId).setCronExpression("0 0 2 * * ?")
                        .setUser("testUser2").build();
                future = Patterns.ask(actorRef, modifyJobRequest, TIMEOUT);
                try {
                    ServerModifyJobResponse response = (ServerModifyJobResponse) Await.result(future, TIMEOUT.duration());
                    Assert.assertTrue(response.getSuccess());
                } catch (Exception e) {
                    Assert.assertTrue(false);
                }

                actorRef.tell(new RemoveJobRequest(jobId), getRef());
                expectMsgEquals("remove success");
            }
        };
    }
}