package com.mogujie.jarvis.server.actor;

import com.google.inject.Injector;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.server.dispatcher.TaskManager;
import com.mogujie.jarvis.server.dispatcher.PriorityTaskQueue;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.dag.DAGScheduler;
import com.mogujie.jarvis.server.scheduler.dag.JobGraph;
import com.mogujie.jarvis.server.scheduler.task.TaskGraph;
import com.mogujie.jarvis.server.scheduler.task.TaskScheduler;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.TaskDependService;
import com.mogujie.jarvis.server.service.TaskService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Location www.mogujie.com
 * Created by qinghuo on 16/1/13.
 * used by jarvis-parent
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Injectors.class})
@SuppressStaticInitializationFor("com.mogujie.jarvis.server.guice.Injectors")
public class TestTaskBase {
    protected static DAGScheduler dagScheduler;
    protected static TaskScheduler taskScheduler;
    protected static JobSchedulerController controller;
    protected static JobGraph jobGraph;
    protected static TaskGraph taskGraph;
    protected static PriorityTaskQueue taskQueue;
    protected static Configuration conf = ConfigUtils.getServerConfig();
    protected static TaskManager taskManager = new TaskManager();
    protected static TaskService taskService = new TaskService();
    protected static JobService jobService = new JobService();
    protected static TaskDependService taskDependService = new TaskDependService();
    protected Injector injector;

    @Before
    public void setup() {
        injector = mock(Injector.class);
        mockStatic(Injectors.class);
        when(Injectors.getInjector()).thenReturn(injector);

    }
}
