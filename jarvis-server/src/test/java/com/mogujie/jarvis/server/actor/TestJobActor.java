package com.mogujie.jarvis.server.actor;

import static org.junit.Assert.*;

import java.io.File;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.mogujie.jarvis.core.domain.CommonStrategy;
import com.mogujie.jarvis.core.domain.JobPriority;
import com.mogujie.jarvis.core.domain.JobRelationType;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.domain.OperationMode;
import com.mogujie.jarvis.core.expression.ScheduleExpressionType;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.core.util.IPUtils;
import com.mogujie.jarvis.dao.generate.JobMapper;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.dto.generate.JobDepend;
import com.mogujie.jarvis.dto.generate.JobDependKey;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.JobDependencyEntryProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.JobProtos.JobStatusEntry;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobDependRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobStatusRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestQueryJobRelationRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestRemoveJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestSubmitJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.ServerModifyJobDependResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerModifyJobResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerModifyJobStatusResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerQueryJobRelationResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerRemoveJobResponse;
import com.mogujie.jarvis.protocol.JobProtos.ServerSubmitJobResponse;
import com.mogujie.jarvis.protocol.JobScheduleExpressionEntryProtos.ScheduleExpressionEntry;
import com.mogujie.jarvis.server.actor.base.DBTestBased;
import com.mogujie.jarvis.server.actor.util.TestUtil;
import com.mogujie.jarvis.server.domain.JobEntry;
import com.mogujie.jarvis.server.guice4test.Injectors4Test;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.util.AppTokenUtils;
import com.mogujie.jarvis.server.util.FutureUtils;
import com.typesafe.config.Config;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;

/**
 * Location www.mogujie.com
 * Created by qinghuo on 16/1/15.
 * used by jarvis-parent
 */
public class TestJobActor extends DBTestBased {
    Config config = ConfigUtils.getAkkaConfig("akka-test.conf");
    String authKey = AppTokenUtils.generateToken(new Date().getTime(), "00416f0458d2482994d70be4b32ab756");
    ActorSystem system;
    JobService jobService;
    Connection conn = null;
    Boolean result = Boolean.FALSE;
    IDatabaseConnection iconn = null;
    String serverHost = "10.11.6.129";
    //    String actorPath = "akka.tcp://server@" + IPUtils.getIPV4Address() + ":10000/user/server";
    String actorPath = "akka.tcp://server@" + serverHost + ":10000/user/server";

    private Thread threadServer;

    @Test
    public void testGetJobDepend() {
        JobDependKey jobDependKey = new JobDependKey();
        jobDependKey.setJobId(2L);
        jobDependKey.setPreJobId(1L);
        jobService = Injectors4Test.getInjector().getInstance(JobService.class);
        JobDepend jobDepend = jobService.getJobDepend(jobDependKey);
        assertEquals((int) jobDepend.getCommonStrategy(), 2);
        assertEquals(jobDepend.getOffsetStrategy(), "cd");
    }

    /**
     * 导出需要备份的表的数据，以便恢复使用
     */
    public void testExportTable() {
        String[] tableNames = new String[] { "app", "worker", "job_depend", "job_schedule_expression" };
        try {
            iconn = getIDatabaseConnection();
            conn = iconn.getConnection();
            conn.setAutoCommit(false);
            for (String name : tableNames) {
                File file = new File("src/test/resources/dataForExport/" + name + ".xml");
                exportTable(file, conn, name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void prepareData(IDatabaseConnection iconn, String tableName) throws Exception {
        //Remove the data from table app
        execSql(iconn, "delete from " + tableName);
        //INSERT TEST DATA
        ReplacementDataSet createDataSet = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("AppDB.xml"));
        DatabaseOperation.INSERT.execute(iconn, createDataSet);

    }

    public void testDBUnit() {
        try {
            iconn = getIDatabaseConnection();
            conn = iconn.getConnection();
            conn.setAutoCommit(false);
            prepareData(iconn, "app");
            ReplacementDataSet dataload_result = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("AppDB.xml"));
            assertDataSet("app", "select * from app where appId=1", dataload_result, iconn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ActorSystem getActorSystem() {
        Config akkaConfig = ConfigUtils.getAkkaConfig("akka-test.conf");
        system = ActorSystem.create("test", akkaConfig);
        return system;
    }

    public ActorSelection getServerActor(ActorSystem system, String serverpath) {
        return system.actorSelection(serverpath);

    }

    /**
     * a
     * |
     * b
     */
    @Test
    public void testSubmitJob() {
        // startUpServer();
        AppAuth appAuth = AppAuth.newBuilder().setToken("11111").setName("jarvis-web").build();

        system = getActorSystem();
        ActorSelection serverActor = getServerActor(system, actorPath);
        RestRemoveJobRequest removeJobRequest;
        //添加任务依赖
        DependencyEntry dependencyEntry = DependencyEntry.newBuilder().setCommonDependStrategy(CommonStrategy.ALL.getValue()).setJobId(2L)
                .setOffsetDependStrategy("d(3)").setOperator(OperationMode.ADD.getValue()).build();
        //添加时间依赖
        ScheduleExpressionEntry expressionEntry = ScheduleExpressionEntry.newBuilder().setExpressionType(ScheduleExpressionType.CRON.getValue())
                .setOperator(OperationMode.ADD.getValue()).setScheduleExpression("0 11 3 * * ?").setExpressionId(35L).build();

        //添加依赖列表
        List<DependencyEntry> dependencyEntryList = new ArrayList<DependencyEntry>();
        List<ScheduleExpressionEntry> expressionEntries = new ArrayList<>();

        dependencyEntryList.add(dependencyEntry);
        expressionEntries.add(expressionEntry);
        RestSubmitJobRequest request = RestSubmitJobRequest.newBuilder().addAllDependencyEntry(dependencyEntryList)
                .addAllExpressionEntry(expressionEntries).setJobName("qh_test").setAppName("jarvis-web").setAppAuth(appAuth)
                .setContent("show databases;").setPriority(JobPriority.HIGH.getValue()).setParameters("{\"para1\":\"1\",\"para2\":\"2\"}")
                .setStatus(JobStatus.ENABLE.getValue()).setUser("qinghuo").setExpiredTime(86400).setFailedAttempts(3).setFailedInterval(3)
                .setBizGroupId(11).setJobType("hive").setWorkerGroupId(1).build();
        ServerSubmitJobResponse response = null;
        for (int i = 0; i < 9; i++) {
            try {

                response = (ServerSubmitJobResponse) FutureUtils.awaitResult(serverActor, request, 15);
                if (response.getSuccess())
                    break;
            } catch (Exception e) {
                System.out.println("server not ready");
            }
        }
        assertTrue(response.getSuccess());
        JobDependKey key = new JobDependKey();
        key.setJobId(response.getJobId());
        jobService = Injectors4Test.getInjector().getInstance(JobService.class);
        JobDepend jobDepend = jobService.getJobDepend(key);
        JobEntry jobEntry = jobService.get(response.getJobId());

        try {
            assertEquals(jobEntry.getJob().getJobName(), "qh_test");
            assertTrue(jobService.isActive(response.getJobId()));
            assertEquals((long) jobDepend.getPreJobId(), 2L);
        } catch (Exception ex) {
        } finally {
            ServerRemoveJobResponse removeJobResponse = null;
            removeJobRequest = RestRemoveJobRequest.newBuilder().setAppAuth(appAuth).setJobId(response.getJobId()).setUser("qinghuo").build();

            try {
                removeJobResponse = (ServerRemoveJobResponse) FutureUtils.awaitResult(serverActor, removeJobRequest, 15);
            } catch (Exception e) {
                e.printStackTrace();
            }

            assertTrue(removeJobResponse.getSuccess());
            //            jobService.deleteJobDepend(response.getJobId(), 2L);
            //            jobService.deleteScheduleExpressionByJobId(response.getJobId());
            //            jobService.deleteJob(response.getJobId());
        }

    }

    @Test
    public void testModifyJob() {
        long jobId = 318L;
        jobService = Injectors4Test.getInjector().getInstance(JobService.class);
        AppAuth appAuth = AppAuth.newBuilder().setToken("11111").setName("jarvis-web").build();

        ServerModifyJobResponse response = null;
        JobEntry oldJob = jobService.get(jobId);
        String newName = "qh";
        String[] oldName = oldJob.getJob().getJobName().split("_");
        if (oldName.length < 2) {
            newName = "qh_test";
        }
        RestModifyJobRequest request = RestModifyJobRequest.newBuilder().setJobId(jobId).setAppName("jarvis-web").setUser("qinghuo")
                .setAppAuth(appAuth).setJobName(newName).build();
        system = getActorSystem();
        ActorSelection serverActor = getServerActor(system, actorPath);
        for (int i = 0; i < 9; i++) {
            try {
                response = (ServerModifyJobResponse) FutureUtils.awaitResult(serverActor, request, 15);
                if (response.getSuccess())
                    break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Job newJob = jobService.getJobMapper().selectByPrimaryKey(jobId);
        assertTrue(response.getSuccess());
        assertNotEquals(oldName, newName);
        assertEquals(newName, newJob.getJobName());
    }

    @After
    public void tearDown() {
        try {
            if (!TestUtil.isPortHasBeenUse(IPUtils.getIPV4Address(), 10010)) {
                while (!system.isTerminated())
                    system.terminate();

            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQueryJobRel() {
        long modifyJobId = 318L;
        ActorSystem system = getActorSystem();
        ActorSelection serverActor = system.actorSelection(actorPath);
        ServerQueryJobRelationResponse response = null;
        AppAuth appAuth = AppAuth.newBuilder().setToken("11111").setName("jarvis-web").build();
        RestQueryJobRelationRequest request = RestQueryJobRelationRequest.newBuilder().setAppAuth(appAuth).setJobId(modifyJobId)
                .setRelationType(JobRelationType.PARENT.getValue()).setUser("qinghuo").build();

        try {
            response = (ServerQueryJobRelationResponse) FutureUtils.awaitResult(serverActor, request, 15);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<JobStatusEntry> entryList = response.getJobStatusEntryList();
        List<Long> entryJobId = Lists.newArrayList();
        List<Integer> statusId = Lists.newArrayList();
        for (JobStatusEntry entry : entryList) {
            entryJobId.add(entry.getJobId());
            statusId.add(entry.getStatus());
        }

        assertArrayEquals(entryJobId.toArray(), new Long[] { 2L, 3L });
        assertArrayEquals(statusId.toArray(), new Integer[] { 1, 1 });
    }

    @Test
    public void modifyJobDependcy() {
        long modifyJobId = 318L;
        long addPreId = 3L;
        long addPreId1 = 2L;
        ServerModifyJobDependResponse response = null;
        ServerModifyJobDependResponse removeResponse = null;
        jobService = Injectors4Test.getInjector().getInstance(JobService.class);
        ActorSystem system = getActorSystem();
        ActorSelection serverActor = getServerActor(system, actorPath);
        AppAuth appAuth = AppAuth.newBuilder().setToken("11111").setName("jarvis-web").build();

        //        DependencyEntry removeDependencyEntry3L = DependencyEntry.newBuilder()
        //                .setOperator(OperationMode.DELETE.getValue())
        //                .setJobId(addPreId)
        //                .setCommonDependStrategy(CommonStrategy.ALL.getValue())
        //                .setOffsetDependStrategy("d(2)")
        //                .build();
        //        DependencyEntry removeDependencyEntry2L = DependencyEntry.newBuilder()
        //                .setOperator(OperationMode.DELETE.getValue())
        //                .setJobId(addPreId1)
        //                .setCommonDependStrategy(CommonStrategy.ANYONE.getValue())
        //                .setOffsetDependStrategy("d(4)")
        //                .build();
        //
        //        List<DependencyEntry> removeDependencyEntries = new ArrayList<>();
        //        removeDependencyEntries.add(removeDependencyEntry3L);
        //        removeDependencyEntries.add(removeDependencyEntry2L);
        //        RestModifyJobDependRequest removeRequest = RestModifyJobDependRequest.newBuilder()
        //                .setUser("qinghuo")
        //                .setAppAuth(appAuth)
        //                .setJobId(modifyJobId)
        //                .addAllDependencyEntry(removeDependencyEntries)
        //                .build();
        //        try {
        //            //首先删除 已经有的depend
        //            removeResponse = (ServerModifyJobDependResponse) FutureUtils.awaitResult(serverActor, removeRequest, 15);
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }

        DependencyEntry dependencyEntry3L = DependencyEntry.newBuilder().setOperator(OperationMode.ADD.getValue()).setJobId(addPreId)
                .setCommonDependStrategy(CommonStrategy.ALL.getValue()).setOffsetDependStrategy("d(2)").build();
        DependencyEntry dependencyEntry2L = DependencyEntry.newBuilder().setOperator(OperationMode.ADD.getValue()).setJobId(addPreId1)
                .setCommonDependStrategy(CommonStrategy.ANYONE.getValue()).setOffsetDependStrategy("d(4)").build();
        List<DependencyEntry> dependencyEntries = new ArrayList<>();

        dependencyEntries.add(dependencyEntry3L);
        dependencyEntries.add(dependencyEntry2L);

        RestModifyJobDependRequest request = RestModifyJobDependRequest.newBuilder().setUser("qinghuo").setAppAuth(appAuth).setJobId(modifyJobId)
                .addAllDependencyEntry(dependencyEntries).build();

        for (int i = 0; i < 9; i++) {
            try {
                response = (ServerModifyJobDependResponse) FutureUtils.awaitResult(serverActor, request, 15);
            } catch (Exception e) {
                System.err.println("server not ready");
            }
            if (response != null && response.getSuccess())
                break;
        }

        JobDependKey newJobDependKey = new JobDependKey();
        newJobDependKey.setJobId(modifyJobId);
        newJobDependKey.setPreJobId(addPreId);
        JobDepend newJobDepend = jobService.getJobDepend(newJobDependKey);

        assertNotNull(newJobDepend);
        assertEquals((long) newJobDepend.getPreJobId(), addPreId);
    }

    @Test
    public void modifyJobRemove() {
        long removeJobId = 361L;
        ServerModifyJobStatusResponse response = null;
        AppAuth appAuth = AppAuth.newBuilder().setToken("11111").setName("jarvis-web").build();
        RestModifyJobStatusRequest request = RestModifyJobStatusRequest.newBuilder().setAppAuth(appAuth).setStatus(JobStatus.DELETED.getValue())
                .setUser("qinghuo").setJobId(removeJobId).build();
        system = getActorSystem();
        ActorSelection serverActor = getServerActor(system, actorPath);
        try {
            response = (ServerModifyJobStatusResponse) FutureUtils.awaitResult(serverActor, request, 30);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //assertTrue(response.getSuccess());
        jobService = Injectors4Test.getInjector().getInstance(JobService.class);
        JobMapper jobMapper = jobService.getJobMapper();
        int status = jobMapper.selectByPrimaryKey(removeJobId).getStatus();
        assertEquals(JobStatus.DELETED.getValue(), status);

    }

    public void removeJob() {
        ActorSystem system = getActorSystem();
        ActorSelection serverActor = system.actorSelection(actorPath);
        AppAuth appAuth = AppAuth.newBuilder().setToken("11111").setName("jarvis-web").build();

        //        List<ScheduleExpressionEntry> expressionEntries = Lists.newArrayList();
        ////添加时间依赖
        //        ScheduleExpressionEntry expressionEntry = ScheduleExpressionEntry.newBuilder()
        //                .setExpressionType(ScheduleExpressionType.ISO8601.getValue())
        //                .setOperator(OperationMode.DELETE.getValue())
        //                .setExpressionId(66)
        //                .build();
        //
        //        List<ScheduleExpressionEntry> removeEntries = Lists.newArrayList();
        //        removeEntries.add(expressionEntry);
        //        RestModifyJobScheduleExpRequest request = RestModifyJobScheduleExpRequest.newBuilder()
        //                .setUser("qinghuo")
        //                .setAppAuth(appAuth)
        //                .setJobId(360L)
        //                .addAllExpressionEntry(removeEntries)
        //                .build();
        //        expressionEntries.add(expressionEntry);
        RestRemoveJobRequest request = RestRemoveJobRequest.newBuilder().setUser("qinghuo").setAppAuth(appAuth).setJobId(360L).build();

        try {
            FutureUtils.awaitResult(serverActor, request, 15);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
