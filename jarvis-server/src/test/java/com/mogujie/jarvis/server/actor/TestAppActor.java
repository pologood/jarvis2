package com.mogujie.jarvis.server.actor;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import com.mogujie.jarvis.protocol.ApplicationProtos.RestServerCreateApplicationRequest;
import com.mogujie.jarvis.server.JarvisServerActorSystem;
import com.mogujie.jarvis.server.util.SpringContext;
import com.mogujie.jarvis.server.util.SpringExtension;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;

@ContextConfiguration(locations = "classpath:context.xml")
public class TestAppActor extends AbstractTransactionalJUnit4SpringContextTests {

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        ApplicationContext context = SpringContext.getApplicationContext();
        system = JarvisServerActorSystem.getInstance();
        SpringExtension.SPRING_EXT_PROVIDER.get(system).initialize(context);
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
    }

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
