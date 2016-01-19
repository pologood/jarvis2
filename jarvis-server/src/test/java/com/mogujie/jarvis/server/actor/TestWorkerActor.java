package com.mogujie.jarvis.server.actor;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.google.inject.Injector;
import com.mogujie.jarvis.core.util.ConfigUtils;
<<<<<<< HEAD
import com.mogujie.jarvis.dto.generate.Worker;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos;
=======
import com.mogujie.jarvis.protocol.WorkerProtos;
>>>>>>> 778423ff2ebc37dbcf660d105a7d0261b02b7175
import com.mogujie.jarvis.server.JarvisServer;
import com.mogujie.jarvis.server.actor.base.DBTestBased;
import com.mogujie.jarvis.server.actor.util.TestUtil;
import com.mogujie.jarvis.server.guice4test.Injectors4Test;
import com.mogujie.jarvis.server.service.WorkerService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValueFactory;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mybatis.guice.transactional.Transactional;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Location www.mogujie.com
 * Created by qinghuo on 16/1/14.
 * used by jarvis-parent
 */
public class TestWorkerActor extends DBTestBased {
    String authKey = "d03fa97612734db7bdee3bbb2cdbf993";
    Thread threadServer = null;
    Connection conn = null;
    Boolean result = Boolean.FALSE;
    IDatabaseConnection iconn = null;
    int registPort = 10004;
    Injector injector = Injectors4Test.getInjector();
    WorkerService workerService = injector.getInstance(WorkerService.class);
    ActorSystem system;

    @Before
    public void setup() {
        try {//检测server端口是否被占用
            if (TestUtil.isPortHasBeenUse("localhost", 10000) && TestUtil.isPortHasBeenUse(InetAddress.getLocalHost().getHostAddress(), 10000)) {
                ServerProxy serverProxy = new ServerProxy();
                threadServer = new Thread(serverProxy);
                threadServer.start();
            }
        } catch (IOException e) {
            System.err.println("no port to use");
        }


    }

    @Transactional
    @Test
    public void testModifyWorkerStatus() {
        Config akkaConfig = ConfigUtils.getAkkaConfig("akka-test.conf");
        String portPath = "akka.remote.netty.tcp.port";
        akkaConfig = akkaConfig.withValue(portPath, ConfigValueFactory.fromAnyRef(registPort));
        system = ActorSystem.create("worker", akkaConfig);
        ActorSelection serverActor = system.actorSelection("akka.tcp://server@127.0.0.1:10000/user/server");

        try {
            iconn = getIDatabaseConnection();
            conn = iconn.getConnection();
            conn.setAutoCommit(false);
            prepareData(iconn, "worker");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ModifyProxy proxy1 = new ModifyProxy(10006);
        Thread t1 = new Thread(proxy1);

    }

    @After
    public void tearDown() {
        system.shutdown();
        if (threadServer != null) threadServer.interrupt();
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


    @Test
    @Transactional
    public void testWorkerRegister() {
        //测试绑定10003至10004端口
        Config akkaConfig = ConfigUtils.getAkkaConfig("akka-test.conf");
        String portPath = "akka.remote.netty.tcp.port";
        akkaConfig = akkaConfig.withValue(portPath, ConfigValueFactory.fromAnyRef(registPort));
        system = ActorSystem.create("worker", akkaConfig);
        ActorSelection serverActor = system.actorSelection("akka.tcp://server@127.0.0.1:10000/user/server");

        new JavaTestKit(system) {{
            WorkerProtos.WorkerRegistryRequest workerRegistryRequest = WorkerProtos.WorkerRegistryRequest.newBuilder().setKey(authKey).build();
            int flag = 0;
            while (flag < 10) {
                try {
                    serverActor.tell(workerRegistryRequest, getRef());

<<<<<<< HEAD
                    RegistryWorkerProtos.ServerRegistryResponse response
                            = (RegistryWorkerProtos.ServerRegistryResponse) receiveOne(duration("3 seconds"));
=======
                    WorkerProtos.ServerRegistryResponse response
                            = (WorkerProtos.ServerRegistryResponse) receiveOne(duration("3 seconds"));
                    //  if(serverActor.path())
>>>>>>> 778423ff2ebc37dbcf660d105a7d0261b02b7175
                    if (response.getSuccess()) {
                        Assert.assertEquals(response.getSuccess(), true);
                        break;
                    }
                    Assert.assertEquals(response.getSuccess(), true);

                } catch (NullPointerException ex) {
                    System.err.println("server not ready");
                }

                flag++;
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }};


        Assert.assertEquals(workerService.getWorkerId("127.0.0.1", registPort), 11);
    }

    @Override
    protected void prepareData(IDatabaseConnection iconn, String tableName) throws Exception {

        //Remove the data from table app
        execSql(iconn, "delete from " + tableName);
        //INSERT TEST DATA
        String fileName = "dataForExport/back_" + tableName + ".xml";
        ReplacementDataSet createDataSet = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
        DatabaseOperation.INSERT.execute(iconn, createDataSet);
    }

    class ModifyProxy implements Runnable {
        Config akkaConfig = ConfigUtils.getAkkaConfig("akka-test.conf");
        String portPath = "akka.remote.netty.tcp.port";
        private int port;

        public ModifyProxy(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            akkaConfig = akkaConfig.withValue(portPath, ConfigValueFactory.fromAnyRef(port));
            ActorSystem system = ActorSystem.create("worker", akkaConfig);

            ActorSelection serverActor = system.actorSelection("akka.tcp://server@127.0.0.1:10000/user/server");

            ModifyWorkerStatusProtos.RestServerModifyWorkerStatusRequest request = ModifyWorkerStatusProtos.RestServerModifyWorkerStatusRequest.newBuilder().
                    setStatus(3).build();
            new JavaTestKit(system) {{
                serverActor.tell(request, getRef());

                ModifyWorkerStatusProtos.ServerModifyWorkerStatusResponse response = (ModifyWorkerStatusProtos.ServerModifyWorkerStatusResponse) receiveOne(duration("3 seconds"));

                Assert.assertTrue(response.getSuccess());

                int workerId = (int) workerService.getWorkerId("127.0.0.1", port);

                Worker worker = workerService.getWorkerMapper().selectByPrimaryKey(workerId);

                Assert.assertEquals((int) worker.getStatus(), 3);
            }};
        }


    }
}

class ServerProxy implements Runnable {

    @Override
    public void run() {
        String[] s = new String[0];
        try {
            new JarvisServer().main(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

