package com.mogujie.jarvis.server.actor;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.mogujie.jarvis.core.domain.CommonStrategy;
import com.mogujie.jarvis.core.domain.JobPriority;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.domain.OperationMode;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.core.util.IPUtils;
import com.mogujie.jarvis.dto.generate.JobDepend;
import com.mogujie.jarvis.dto.generate.JobDependKey;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.JobDependencyEntryProtos.DependencyEntry;
import com.mogujie.jarvis.protocol.JobProtos.RestSubmitJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.ServerSubmitJobResponse;
import com.mogujie.jarvis.server.actor.base.DBTestBased;
import com.mogujie.jarvis.server.domain.JobEntry;
import com.mogujie.jarvis.server.guice4test.Injectors4Test;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.util.AppTokenUtils;
import com.mogujie.jarvis.server.util.FutureUtils;
import com.typesafe.config.Config;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Location www.mogujie.com
 * Created by qinghuo on 16/1/15.
 * used by jarvis-parent
 */
public class TestJobActor extends DBTestBased {
    Config config = ConfigUtils.getAkkaConfig("akka-test.conf");
    String authKey = AppTokenUtils.generateToken(new Date().getTime(), "00416f0458d2482994d70be4b32ab756");
    ActorSystem system;
    JobService jobService ;
    Connection conn = null;
    Boolean result = Boolean.FALSE;
    IDatabaseConnection iconn = null;

    @Test
    public void testGetJobDepend() {
        JobDependKey jobDependKey = new JobDependKey();
        jobDependKey.setJobId(2L);
        jobDependKey.setPreJobId(1L);
        JobDepend jobDepend = jobService.getJobDepend(jobDependKey);
        assertEquals((int) jobDepend.getCommonStrategy(), 2);
        assertEquals(jobDepend.getOffsetStrategy(), "cd");
    }

    /**
     * 导出需要备份的表的数据，以便恢复使用
     */
    public void testExportTable() {
        String[] tableNames = new String[]{"app", "worker", "job_depend", "job_schedule_expression"};
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

    @Test
    public void testDBUnit() {
        try {
            iconn = getIDatabaseConnection();
            conn = iconn.getConnection();
            conn.setAutoCommit(false);
            prepareData(iconn, "app");
            ReplacementDataSet dataload_result =
                    createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream("AppDB.xml"));
            assertDataSet("app", "select * from app where appId=1", dataload_result, iconn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * a
     * |
     * b
     */

    @Test
    public void testSubmitJob() {
        AppAuth appAuth = AppAuth.newBuilder().setToken("e162b634a881453fb26149cbcb68b2a7").setName("jarvis-web").build();
        Config akkaConfig = ConfigUtils.getAkkaConfig("akka-test.conf");
        system = ActorSystem.create("rest", akkaConfig);
        String actorPath = "akka.tcp://server@" + IPUtils.getIPV4Address() + ":10000/user/server";
        ActorSelection serverActor = system.actorSelection(actorPath);
        DependencyEntry dependencyEntry = DependencyEntry.newBuilder()
                .setCommonDependStrategy(CommonStrategy.ALL.getValue())
                .setJobId(2L).setOffsetDependStrategy("d(3)")
                .setOperator(OperationMode.ADD.getValue())
                .build();
        //添加依赖
        List<DependencyEntry> dependencyEntryList = new ArrayList<DependencyEntry>();
        dependencyEntryList.add(dependencyEntry);
        RestSubmitJobRequest request = RestSubmitJobRequest.newBuilder()
                .addAllDependencyEntry(dependencyEntryList)
                .setJobName("qh_test")
                .setAppName("jarvis-web")
                .setAppAuth(appAuth)
                .setContent("use testing")
                .setPriority(JobPriority.HIGH.getValue())
                .setParameters("{\"para1\":\"1\",\"para2\":\"2\"}")
                .setStatus(JobStatus.ENABLE.getValue())
                .setUser("qinghuo")
                .setExpiredTime(86400).setFailedAttempts(3)
                .setFailedInterval(3)
                .setBizGroupId(11).setJobType("hive script")
                .setWorkerGroupId(1)
                .build();
        ServerSubmitJobResponse response = null;
        try {
            response = (ServerSubmitJobResponse) FutureUtils.awaitResult(serverActor, request, 30);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(response.getSuccess());
        JobDependKey key = new JobDependKey();
        key.setJobId(response.getJobId());
        jobService=Injectors4Test.getInjector().getInstance(JobService.class);
        JobDepend jobDepend = jobService.getJobDepend(key);
        JobEntry jobEntry=jobService.get(response.getJobId());
        try {
            assertEquals(jobEntry.getJob().getJobName(), "qh_test");
            assertTrue(jobService.isActive(response.getJobId()));
            assertEquals((long) jobDepend.getPreJobId(), 2L);
        } catch (Exception ex) {
        } finally {
            jobService.deleteJob(response.getJobId());
            jobService.deleteJobDepend(response.getJobId(), 2L);
        }


    }



    @After
    public void tearDown() {
        system.shutdown();
        //just remember to rollback database
        if (conn != null) {
            try {
                conn.rollback();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    class SubmitJobThread implements Runnable {

        @Override
        public void run() {
            ActorSelection serverActor = system.actorSelection("akka.tcp://server@127.0.0.1:10000/user/server");

        }
    }
}
