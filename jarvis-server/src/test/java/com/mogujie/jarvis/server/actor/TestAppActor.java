package com.mogujie.jarvis.server.actor;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import akka.actor.ActorRef;
import akka.testkit.JavaTestKit;

import com.mogujie.jarvis.protocol.ApplicationProtos.RestServerCreateApplicationRequest;
import com.mogujie.jarvis.server.util.SpringExtension;

public class TestAppActor extends TestBaseActor {

    @Test
    @Rollback(true)
    @Transactional
    public void test1() {
        new JavaTestKit(system) {
            {
                final ActorRef ref = system.actorOf(SpringExtension.SPRING_EXT_PROVIDER.get(system).props("appActor"));
                RestServerCreateApplicationRequest request = RestServerCreateApplicationRequest.newBuilder().setAppName("test-2")
                        .setMaxConcurrency(789).setStatus(0).setUser("wuya").build();
                ref.tell(request, getRef());
            }
        };
    }
}
