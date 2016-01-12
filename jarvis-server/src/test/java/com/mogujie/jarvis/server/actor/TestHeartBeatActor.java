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
import com.mogujie.jarvis.worker.WorkerConfigKeys;
import com.mogujie.jarvis.worker.actor.WorkerActor;
import com.typesafe.config.Config;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;

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
        Config akkaConfig = ConfigUtils.getAkkaConfig("akka-worker.conf");
        Configuration workerConfig = ConfigUtils.getWorkerConfig();
        system = ActorSystem.create(JarvisConstants.WORKER_AKKA_SYSTEM_NAME, akkaConfig);
        String serverAkkaPath = workerConfig.getString(WorkerConfigKeys.SERVER_AKKA_PATH) + JarvisConstants.SERVER_AKKA_USER_PATH;
        Props props = HeartBeatActor.props();
        new JavaTestKit(system) {
            {final JavaTestKit probe = new JavaTestKit(system);

                ActorSelection heartBeatActor = system.actorSelection(serverAkkaPath);
                ActorRef workerActor = system.actorOf(new SmallestMailboxPool(10).props(WorkerActor.props()), JarvisConstants.WORKER_AKKA_SYSTEM_NAME);
                heartBeatActor.tell(heartBeatRequest, getRef());
                expectMsgEquals(duration("1 seconds"), HeartBeatProtos.HeartBeatResponse.class);
            }
        };


    }





}
