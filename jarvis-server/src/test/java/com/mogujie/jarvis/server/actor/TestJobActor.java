package com.mogujie.jarvis.server.actor;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.dto.generate.JobDepend;
import com.mogujie.jarvis.dto.generate.JobDependKey;
import com.mogujie.jarvis.protocol.AppAuthProtos;
import com.mogujie.jarvis.server.actor.base.DBTestBased;
import com.mogujie.jarvis.server.guice4test.Injectors4Test;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.util.AppTokenUtils;
import com.typesafe.config.Config;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Location www.mogujie.com
 * Created by qinghuo on 16/1/15.
 * used by jarvis-parent
 */
public class TestJobActor extends DBTestBased {
    Config config = ConfigUtils.getAkkaConfig("akka-test.conf");
    String authKey = AppTokenUtils.generateToken(new Date().getTime(), "00416f0458d2482994d70be4b32ab756");
    AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName("a").setToken(authKey).build();
    ActorSystem system = ActorSystem.create("worker", config);
    JobService jobService = Injectors4Test.getInjector().getInstance(JobService.class);
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
