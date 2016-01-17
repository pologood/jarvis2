package com.mogujie.jarvis.server.actor;

import akka.actor.*;
import akka.testkit.JavaTestKit;
import com.mogujie.jarvis.dto.generate.Worker;
import com.mogujie.jarvis.protocol.*;
import com.mogujie.jarvis.protocol.WorkerGroupProtos.RestServerModifyWorkerGroupRequest;
import com.mogujie.jarvis.server.actor.base.TestWorkerServiceBase;
import com.mogujie.jarvis.server.util.AppTokenUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Location www.mogujie.com
 * Created by qinghuo on 16/1/12.
 * used by jarvis-parent
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ActorPath.class)
@SuppressStaticInitializationFor("com.mogujie.jarvis.server.guice.Injectors")
public class TestWorkerAndGroupActor extends TestWorkerServiceBase {
    static ActorSystem system = ActorSystem.create("myActor");
    String authKey = AppTokenUtils.generateToken(new Date().getTime(), "workerRegistry");
    AppAuthProtos.AppAuth appAuth = AppAuthProtos.AppAuth.newBuilder().setName("a").setToken(authKey).build();

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




}
