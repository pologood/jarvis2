/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月6日 下午5:40:40
 */

package com.mogujie.jarvis.server.scheduler.dag;

import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.server.scheduler.SchedulerUtil;
import com.mogujie.jarvis.server.scheduler.controller.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.controller.SyncSchedulerController;
import com.mogujie.jarvis.server.scheduler.dag.checker.DAGDependCheckerFactory;
import com.mogujie.jarvis.server.scheduler.dag.checker.DummyDAGDependChecker;
import com.mogujie.jarvis.server.scheduler.task.TaskScheduler;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class TestSchedulerBase {
    protected static DAGScheduler dagScheduler;
    protected static TaskScheduler taskScheduler;
    protected static JobSchedulerController controller;
    protected static JobGraph jobGraph;
    protected static Configuration conf = ConfigUtils.getServerConfig();

    @BeforeClass
    public static void setup() throws Exception {
        conf.clear();
        conf.setProperty(DAGDependCheckerFactory.DAG_DEPEND_CHECKER_KEY,
                DummyDAGDependChecker.class.getName());
        conf.setProperty(SchedulerUtil.ENABLE_TEST_MODE, true);
        controller = SyncSchedulerController.getInstance();
        dagScheduler = SpringContext.getBean(DAGScheduler.class);
        taskScheduler = SpringContext.getBean(TaskScheduler.class);
        controller.register(dagScheduler);
        controller.register(taskScheduler);
        jobGraph = dagScheduler.getJobGraph();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        controller.unregister(dagScheduler);
        controller.unregister(taskScheduler);
    }

    @After
    public void tearDown() throws Exception {
        dagScheduler.destroy();
        taskScheduler.destroy();
    }
}
