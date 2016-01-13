package com.mogujie.jarvis.server.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.testkit.JavaTestKit;
import com.mogujie.jarvis.dto.generate.Worker;
import com.mogujie.jarvis.protocol.AppAuthProtos;
import com.mogujie.jarvis.protocol.ModifyWorkerStatusProtos;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos;
import com.mogujie.jarvis.protocol.WorkerGroupProtos;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.RestServerModifyWorkerGroupRequest;
import com.mogujie.jarvis.server.util.AppTokenUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Location www.mogujie.com
 * Created by qinghuo on 16/1/12.
 * used by jarvis-parent
 */
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("com.mogujie.jarvis.server.guice.Injectors")
public class TestWorkerAndGroupActor extends TestWorkerServiceBase {
    static ActorSystem system = ActorSystem.create("myActor");
    String authKey = AppTokenUtils.generateToken(new Date().getTime(), "workerRegistry");
    AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName("a").setToken(authKey).build();


//    @Test
//    public void testWorkerRegisterActor() {
// HeartBeatProtos.HeartBeatResponse response = HeartBeatProtos.HeartBeatResponse.newBuilder().setSuccess(false).build();

//        when(injector.getInstance(WorkerRegistry.class)).thenReturn(workerRegistry);
//        List<WorkerGroup> workerGroups = new ArrayList<WorkerGroup>();
//        WorkerGroup workerGroup = new WorkerGroup();
//        workerGroup.setId(1);
//        workerGroups.add(workerGroup);
//        Address address = new Address("akka", "myActor", "127.0.0.1", 10000);
//
//
//        try {
//            when(workerGroupMapper, "selectByExample", any()).thenReturn(workerGroups);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        RegistryWorkerProtos.WorkerRegistryRequest workerRegistryRequest = RegistryWorkerProtos.WorkerRegistryRequest.newBuilder().setKey(authKey).build();
//        new JavaTestKit(system) {{
//
//            Props props = WorkerRegistryActor.props().withDeploy(new Deploy(new RemoteScope(address)));
//            ActorRef serverActor = system.actorOf(props, "register");
//            Props propsTell = Props.create(MessageTeller.class);
//            ActorRef actorRef = system.actorOf(propsTell);
//            // system.actorOf(props, "register");
//            serverActor.tell(workerRegistryRequest, getRef());
//
//            expectMsgEquals(RegistryWorkerProtos.ServerRegistryResponse.class);
//
//
//        }};
//    }

    @AfterClass
    public static void tearDown() {
        JavaTestKit.shutdownActorSystem(system);
    }

    @Before
    public void ready() {
    }

    @Test
    public void testCreateWorkerGroup() {
        system = ActorSystem.create("myActor");
        WorkerGroupProtos.RestServerCreateWorkerGroupRequest createWorkerGroupRequest = WorkerGroupProtos.RestServerCreateWorkerGroupRequest
                .newBuilder().setWorkerGroupName("group1").setUser("qh").setAppAuth(appAuth).build();
        Props props = WorkerGroupActor.props();
        try {
            when(workerGroupMapper, "insertSelective", any()).thenReturn(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new JavaTestKit(system) {{
            ActorRef createActor = system.actorOf(props, "create");
            createActor.tell(createWorkerGroupRequest, getRef());
            WorkerGroupProtos.ServerCreateWorkerGroupResponse response = (WorkerGroupProtos.ServerCreateWorkerGroupResponse) receiveOne(duration("3 seconds"));
            Assert.assertEquals(response.getSuccess(), true);
        }};

    }

    @Test
    public void testWorkerModifyStatusActor() {
        system = ActorSystem.create("myActor");
        ModifyWorkerStatusProtos.RestServerModifyWorkerStatusRequest modifyWorkerStatusRequest = ModifyWorkerStatusProtos.RestServerModifyWorkerStatusRequest
                .newBuilder().setStatus(1).setIp("127.0.0.1").setPort(10002).setAppAuth(appAuth).build();
        List<Worker> workers = new ArrayList<>();
        Worker worker = new Worker();
        worker.setId(1);
        workers.add(worker);
        try {
            when(workerMapper, "selectByExample", any()).thenReturn(workers);
            when(workerMapper, "updateByPrimaryKeySelective", any()).thenReturn(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new JavaTestKit(system) {{

            Props props = WorkerModifyStatusActor.props();
            ActorRef actorRef = system.actorOf(props, "modify");
            actorRef.tell(modifyWorkerStatusRequest, getRef());
            ModifyWorkerStatusProtos.ServerModifyWorkerStatusResponse response =
                    (ModifyWorkerStatusProtos.ServerModifyWorkerStatusResponse)
                            receiveOne(duration("3 seconds"));
            Assert.assertEquals(response.getSuccess(), true);
        }};
        system.shutdown();
    }


    @Test
    public void testUpdateWorkerGroup() {
        system = ActorSystem.create("myActor");
        try {
            when(workerGroupMapper, "updateByPrimaryKeySelective", any()).thenReturn(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RestServerModifyWorkerGroupRequest request = RestServerModifyWorkerGroupRequest.newBuilder().setAppAuth(appAuth).setUser("qh")
                .setWorkerGroupName("group1").setStatus(1).setWorkerGroupId(1).build();
        Props props = WorkerGroupActor.props();
        new JavaTestKit(system) {{
            ActorRef updateActor = system.actorOf(props, "update");
            updateActor.tell(request, getRef());
            WorkerGroupProtos.ServerModifyWorkerGroupResponse response = (WorkerGroupProtos.ServerModifyWorkerGroupResponse) receiveOne(duration("3 seconds"));
            Assert.assertEquals(response.getSuccess(), true);
        }};
    }

    static class MessageTeller extends UntypedActor implements Runnable {
        ActorSystem system = ActorSystem.create("newActor");
        Props props = WorkerRegistryActor.props();
        ActorRef actorRef = system.actorOf(props);
        transient int flag = 1;

        @Override
        public void run() {
            if (flag != 1) {
                System.out.println("thread stop");
                Thread.currentThread().stop();
            }
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof RegistryWorkerProtos.WorkerRegistryRequest) {
                actorRef.tell(message, getSender());
            } else if (message instanceof RegistryWorkerProtos.ServerRegistryResponse) {
                flag++;
                System.out.println("message receive");
                getSender().tell(message, getSender());
            } else unhandled(message);
        }
    }


}
