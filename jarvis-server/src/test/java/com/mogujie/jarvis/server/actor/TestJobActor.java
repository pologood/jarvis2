package com.mogujie.jarvis.server.actor;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.protocol.DependencyEntryProtos;
import com.mogujie.jarvis.protocol.JobProtos;
import com.typesafe.config.Config;
import org.junit.Test;

/**
 * Location www.mogujie.com
 * Created by qinghuo on 16/1/15.
 * used by jarvis-parent
 */
public class TestJobActor {
    Config config = ConfigUtils.getAkkaConfig("akka-test.conf");

    ActorSystem system = ActorSystem.create("worker", config);
    @Test
    public void testSubmitJob() {

        DependencyEntryProtos.DependencyEntry dependencyEntry = DependencyEntryProtos.DependencyEntry.newBuilder().setJobId(2).build();
        JobProtos.RestSubmitJobRequest submitJobRequest = JobProtos.RestSubmitJobRequest.newBuilder().setDependencyEntry(0, dependencyEntry).build();
        new JavaTestKit(system) {{
            ActorSelection serverActor = system.actorSelection("akka.tcp://server@127.0.0.1:10000/user/server");

        }};

        class SubmitJobThread implements Runnable{

            @Override
            public void run() {
                ActorSelection serverActor = system.actorSelection("akka.tcp://server@127.0.0.1:10000/user/server");

            }
        }
    }

}
