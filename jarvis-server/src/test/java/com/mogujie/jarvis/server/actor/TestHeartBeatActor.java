package com.mogujie.jarvis.server.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.SmallestMailboxPool;
import akka.testkit.JavaTestKit;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.protocol.HeartBeatProtos;
import com.mogujie.jarvis.protocol.HeartBeatProtos.HeartBeatRequest;
import com.mogujie.jarvis.server.ServerConigKeys;
import com.mogujie.jarvis.server.guice.Injectors;
import com.typesafe.config.Config;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.Duration;

/**
 * Location www.mogujie.com
 * Created by qinghuo on 16/1/8.
 * used by jarvis-parent
 */


public class TestHeartBeatActor  {
    protected static ActorSystem system;

    HeartBeatRequest heartBeatRequest = HeartBeatRequest.newBuilder().setJobNum(1).build();

    @Test
    public void testHeartBeat() {
    //todo 对于测试是local的模拟，但是实际通信heartbeat需要远程
        Config akkaConfig = ConfigUtils.getAkkaConfig("akka-worker.conf");
        system = ActorSystem.create(JarvisConstants.WORKER_AKKA_SYSTEM_NAME, akkaConfig);
        Props props = HeartBeatActor.props();
        new JavaTestKit(system) {
            {final JavaTestKit probe = new JavaTestKit(system);
                ActorRef actorRef = system.actorOf(props);
                actorRef.tell(heartBeatRequest, getRef());
//                expectMsgEquals(duration("5 seconds"), HeartBeatProtos.HeartBeatResponse.class);
                expectNoMsg();
            }
        };


    }







}
