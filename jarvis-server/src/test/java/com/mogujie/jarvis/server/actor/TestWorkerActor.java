package com.mogujie.jarvis.server.actor;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.google.inject.Injector;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.core.util.IPUtils;
import com.mogujie.jarvis.dto.generate.Worker;
import com.mogujie.jarvis.protocol.AppAuthProtos;
import com.mogujie.jarvis.protocol.WorkerProtos.RestServerModifyWorkerStatusRequest;
import com.mogujie.jarvis.protocol.WorkerProtos.ServerModifyWorkerStatusResponse;
import com.mogujie.jarvis.protocol.WorkerProtos.ServerRegistryResponse;
import com.mogujie.jarvis.protocol.WorkerProtos.WorkerRegistryRequest;
import com.mogujie.jarvis.server.JarvisServer;
import com.mogujie.jarvis.server.actor.util.TestUtil;
import com.mogujie.jarvis.server.guice4test.Injectors4Test;
import com.mogujie.jarvis.server.service.WorkerService;
import com.typesafe.config.Config;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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
    String serverHost = "10.11.6.129";
    AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setToken("11111").setName("jarvis-web").build();

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
    public void testModifyWorkerStatus() throws SocketException, UnknownHostException {
        int workerId = (int) workerService.getWorkerId(IPUtils.getIPV4Address(), registPort);
        Worker worker = workerService.getWorkerMapper().selectByPrimaryKey(workerId);

        int workerStatus = 1;
        if (1 == worker.getStatus()) {
            workerStatus = 2;
        }
        Config akkaConfig = ConfigUtils.getAkkaConfig("akka-test.conf");
        system = ActorSystem.create("worker", akkaConfig);
        String serverPath = "akka.tcp://server@" + serverHost + ":10000/user/server";
        ActorSelection serverActor = system.actorSelection(serverPath);


        RestServerModifyWorkerStatusRequest request = RestServerModifyWorkerStatusRequest
                .newBuilder()
                .setStatus(workerStatus)
                .setIp(IPUtils.getIPV4Address())
                .setPort(registPort)
                .setAppAuth(appAuth).build();

        new JavaTestKit(system) {{
            serverActor.tell(request, getRef());

            ServerModifyWorkerStatusResponse response = (ServerModifyWorkerStatusResponse) receiveOne(duration("10 seconds"));

            Assert.assertTrue(response.getSuccess());

        }};
        workerId = (int) workerService.getWorkerId(IPUtils.getIPV4Address(), registPort);

        worker = workerService.getWorkerMapper().selectByPrimaryKey(workerId);

        Assert.assertEquals((int) worker.getStatus(), workerStatus);

    }


    @Test
    public void testWorkerRegister() throws SocketException, UnknownHostException {
        //测试绑定10001端口
        Config akkaConfig = ConfigUtils.getAkkaConfig("akka-test.conf");
        system = ActorSystem.create("worker", akkaConfig);
        String serverPath = "akka.tcp://server@" + serverHost + ":10000/user/server";
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

