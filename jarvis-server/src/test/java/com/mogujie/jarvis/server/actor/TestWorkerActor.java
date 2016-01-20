package com.mogujie.jarvis.server.actor;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.google.inject.Injector;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.core.util.IPUtils;
import com.mogujie.jarvis.dto.generate.Worker;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos;
import com.mogujie.jarvis.protocol.WorkerProtos.ServerRegistryResponse;
import com.mogujie.jarvis.protocol.WorkerProtos.WorkerRegistryRequest;
import com.mogujie.jarvis.server.JarvisServer;
import com.mogujie.jarvis.server.actor.util.TestUtil;
import com.mogujie.jarvis.server.guice4test.Injectors4Test;
import com.mogujie.jarvis.server.service.WorkerService;
import com.mogujie.jarvis.server.util.FutureUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValueFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Location www.mogujie.com
 * Created by qinghuo on 16/1/14.
 * used by jarvis-parent
 */
public class TestWorkerActor {
    String authKey = "ec80df2716a547b89d99a3d135dea1d3";
    Thread threadServer = null;
    int registPort = 10001;
    Injector injector = Injectors4Test.getInjector();
    WorkerService workerService = injector.getInstance(WorkerService.class);
    ActorSystem system;

    @Before
    public void setup() {
        try {
//检测server端口是否被占用
            if (TestUtil.isPortHasBeenUse("localhost", 10000) && TestUtil.isPortHasBeenUse(InetAddress.getLocalHost().getHostAddress(), 10000)
                    && TestUtil.isPortHasBeenUse(IPUtils.getIPV4Address(), 10000)) {
                ServerProxy serverProxy = new ServerProxy();
                threadServer = new Thread(serverProxy);
                threadServer.start();
            }
        } catch (IOException e) {
            System.err.println("no port to use");
        }


    }

    @Test
    public void testModifyWorkerStatus() {
        Config akkaConfig = ConfigUtils.getAkkaConfig("akka-test.conf");
        String portPath = "akka.remote.netty.tcp.port";
        String hostPath = "akka.remote.netty.tcp.hostname";
        String hostname = IPUtils.getIPV4Address();
        akkaConfig = akkaConfig.withValue(portPath, ConfigValueFactory.fromAnyRef(registPort));
        akkaConfig = akkaConfig.withValue(hostPath, ConfigValueFactory.fromAnyRef(hostname));
         system = ActorSystem.create("worker", akkaConfig);
        ActorSelection serverActor = system.actorSelection("akka.tcp://server@192.168.21.82:10000/user/server");


        ModifyProxy proxy1 = new ModifyProxy(10006);
        Thread t1 = new Thread(proxy1);

    }

    @After
    public void tearDown() {
        //system.shutdown();
        if (threadServer != null) threadServer.interrupt();

    }


    @Test
    public void testWorkerRegister() {
        //测试绑定10003至10004端口
        Config akkaConfig = ConfigUtils.getAkkaConfig("akka-test.conf");
        String portPath = "akka.remote.netty.tcp.port";
        String hostPath = "akka.remote.netty.tcp.hostname";
        String hostname = IPUtils.getIPV4Address();
        // akkaConfig = akkaConfig.withValue(hostPath, ConfigValueFactory.fromAnyRef(hostname));
        akkaConfig = akkaConfig.withValue(portPath, ConfigValueFactory.fromAnyRef(registPort));

         system = ActorSystem.create("worker", akkaConfig);
        String serverPath = "akka.tcp://server@"+IPUtils.getIPV4Address()+":10000/user/server";
        ActorSelection serverActor = system.actorSelection(serverPath);
        WorkerRegistryRequest workerRegistryRequest =
                WorkerRegistryRequest.newBuilder().setKey(authKey).build();

        new JavaTestKit(system) {{

            int flag = 0;
            while (flag < 10) {
                try {
                    serverActor.tell(workerRegistryRequest, getRef());

                    ServerRegistryResponse response
                            = (ServerRegistryResponse) receiveOne(duration("3 seconds"));

                    if (response.getSuccess()) {
                        Assert.assertEquals(response.getSuccess(), true);
                        break;
                    }
                    Assert.assertEquals(response.getSuccess(), true);

                } catch (NullPointerException ex) {
                    System.err.println("server not ready");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                flag++;
                try {
                    Thread.currentThread().sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }};


        Assert.assertEquals(workerService.getWorkerId(IPUtils.getIPV4Address(), registPort), 22);
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

