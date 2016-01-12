package com.mogujie.jarvis.worker.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.protocol.HeartBeatProtos;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos.WorkerRegistryRequest;
import com.typesafe.config.Config;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;

/**
 * Location www.mogujie.com
 * Created by qinghuo on 16/1/11.
 * used by jarvis-parent
 */
public class TestWorkerRegidtryActor {
    HeartBeatProtos.HeartBeatResponse response = HeartBeatProtos.HeartBeatResponse.newBuilder().setSuccess(false).build();

    Config akkaConfig = ConfigUtils.getAkkaConfig("akka-worker.conf");
    Configuration workerConfig = ConfigUtils.getWorkerConfig();
    ActorSystem system = ActorSystem.create(JarvisConstants.WORKER_AKKA_SYSTEM_NAME, akkaConfig);

    @Test
    public void testRegistry() {
        new JavaTestKit(system) {{
            Props props=WorkerActor.props();
            ActorRef actorRef = system.actorOf(props);
            actorRef.tell(response,getRef());
            expectNoMsg();
        }};
    }
}
