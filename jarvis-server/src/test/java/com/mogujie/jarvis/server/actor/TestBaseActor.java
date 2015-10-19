/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年10月14日 下午3:18:38
 */

package com.mogujie.jarvis.server.actor;

import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.Configuration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.protocol.AppAuthProtos.AppAuth;
import com.mogujie.jarvis.server.JarvisServerActorSystem;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.controller.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.controller.SchedulerControllerFactory;
import com.mogujie.jarvis.server.scheduler.controller.SyncSchedulerController;
import com.mogujie.jarvis.server.scheduler.dag.DAGScheduler;
import com.mogujie.jarvis.server.scheduler.dag.checker.DAGDependCheckerFactory;
import com.mogujie.jarvis.server.scheduler.dag.checker.DummyDAGDependChecker;
import com.mogujie.jarvis.server.scheduler.task.TaskScheduler;
import com.mogujie.jarvis.server.scheduler.time.TimeScheduler;
import com.mogujie.jarvis.server.util.SpringContext;
import com.mogujie.jarvis.server.util.SpringExtension;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;

/**
 * @author guangming
 *
 */
@ContextConfiguration(locations = "classpath:context.xml")
public abstract class TestBaseActor extends AbstractTransactionalJUnit4SpringContextTests {
    protected static ActorSystem system;
    protected static final Timeout TIMEOUT = new Timeout(Duration.create(5, TimeUnit.SECONDS));
    protected static Configuration conf = ConfigUtils.getServerConfig();
    protected AppAuth appAuth = AppAuth.newBuilder().setName("testApp1").setToken("").build();
    private static JobSchedulerController controller;
    private static DAGScheduler dagScheduler;
    private static TaskScheduler taskScheduler;
    private static TimeScheduler timeScheduler;

    @BeforeClass
    public static void setup() {
        conf.setProperty(DAGDependCheckerFactory.DAG_DEPEND_CHECKER_KEY, DummyDAGDependChecker.class.getName());
        conf.setProperty(SchedulerControllerFactory.SCHEDULER_CONTROLLER_KEY, SyncSchedulerController.class.getName());
        conf.setProperty(SchedulerUtil.ENABLE_TEST_MODE, true);
        ApplicationContext context = SpringContext.getApplicationContext();
        system = JarvisServerActorSystem.getInstance();
        SpringExtension.SPRING_EXT_PROVIDER.get(system).initialize(context);
        controller = SchedulerControllerFactory.getController();
        dagScheduler = SpringContext.getBean(DAGScheduler.class);
        taskScheduler = SpringContext.getBean(TaskScheduler.class);
        timeScheduler = SpringContext.getBean(TimeScheduler.class);
        controller.register(dagScheduler);
        controller.register(taskScheduler);
        controller.register(timeScheduler);
    }

    @AfterClass
    public static void tearDown() {
        JavaTestKit.shutdownActorSystem(system);
        controller.unregister(dagScheduler);
        controller.unregister(taskScheduler);
        controller.unregister(timeScheduler);
        conf.clear();
    }
}
