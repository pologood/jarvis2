package com.mogujie.jarvis.server.actor;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.protocol.ApplicationProtos.RestServerCreateApplicationRequest;
import com.mogujie.jarvis.server.util.AppTokenUtils;
import com.mogujie.jarvis.server.util.SpringExtension;

import akka.actor.ActorRef;
import akka.testkit.JavaTestKit;

public class TestAppActor extends TestBaseActor {

    @Test
    @Rollback(true)
    @Transactional
    public void test1() {
        new JavaTestKit(system) {
            {
                AppAuth appAuth = AppAuth.newBuilder().setName("jarvis-web")
                        .setToken(AppTokenUtils.generateToken(System.currentTimeMillis() / 1000, "11111")).build();
                final ActorRef ref = system.actorOf(SpringExtension.SPRING_EXT_PROVIDER.get(system).props("appActor"));
                RestServerCreateApplicationRequest request = RestServerCreateApplicationRequest.newBuilder().setAppAuth(appAuth).setAppName("test-2")
                        .setMaxConcurrency(789).setStatus(0).setUser("wuya").build();
                ref.tell(request, getRef());
            }
        };
    }
}
