package com.mogujie.jarvis.server.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.protocol.HeartBeatProtos;
import com.mogujie.jarvis.protocol.HeartBeatProtos.HeartBeatRequest;
import com.typesafe.config.Config;
import org.junit.Test;

/**
 * Location www.mogujie.com
 * Created by qinghuo on 16/1/8.
 * used by jarvis-parent
 */

public class TestHeartBeatActor {
    protected static ActorSystem system;

    HeartBeatRequest heartBeatRequest = HeartBeatRequest.newBuilder().setJobNum(1).build();
    String serverHost = "10.11.6.129";
    String actorPath = "akka.tcp://server@" + serverHost + ":10000/user/server";

    @Test
    public void testHeartBeat() {
        //todo 对于测试是local的模拟，但是实际通信heartbeat需要远程
        Config akkaConfig = ConfigUtils.getAkkaConfig("akka-test.conf");
        system = ActorSystem.create(JarvisConstants.WORKER_AKKA_SYSTEM_NAME, akkaConfig);
        Props props = HeartBeatActor.props();
        ActorSelection serverActor = system.actorSelection(actorPath);
        new JavaTestKit(system) {
            {
                final JavaTestKit probe = new JavaTestKit(system);
                serverActor.tell(heartBeatRequest, getRef());
                HeartBeatProtos.HeartBeatResponse response = (HeartBeatProtos.HeartBeatResponse) receiveOne(duration("5 seconds"));
                expectNoMsg();

            }
        };

    }

}
