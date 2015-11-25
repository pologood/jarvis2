/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月21日 下午4:11:14
 */

package com.mogujie.jarvis.server;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.Lists;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.core.exeception.JobScheduleException;
import com.mogujie.jarvis.core.expression.CronExpression;
import com.mogujie.jarvis.core.expression.FixedDelayExpression;
import com.mogujie.jarvis.core.expression.FixedRateExpression;
import com.mogujie.jarvis.core.expression.ISO8601Expression;
import com.mogujie.jarvis.core.expression.ScheduleExpression;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.server.actor.ServerActor;
import com.mogujie.jarvis.server.domain.JobEntry;
import com.mogujie.jarvis.server.scheduler.AlarmScheduler;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.TaskRetryScheduler;
import com.mogujie.jarvis.server.scheduler.dag.DAGJob;
import com.mogujie.jarvis.server.scheduler.dag.DAGJobType;
import com.mogujie.jarvis.server.scheduler.dag.DAGScheduler;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.task.TaskScheduler;
import com.mogujie.jarvis.server.scheduler.time.TimeScheduler;
import com.mogujie.jarvis.server.scheduler.time.TimeSchedulerFactory;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.service.TaskService;
import com.mogujie.jarvis.server.util.SpringContext;
import com.mogujie.jarvis.server.util.SpringExtension;

import akka.actor.ActorSystem;
import akka.routing.SmallestMailboxPool;

/**
 *
 *
 */
public class JarvisServer {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) throws Exception {
        LOGGER.info("Starting Jarvis server...");

        ApplicationContext context = SpringContext.getApplicationContext();
        ActorSystem system = JarvisServerActorSystem.getInstance();
        SpringExtension.SPRING_EXT_PROVIDER.get(system).initialize(context);

        system.actorOf(new SmallestMailboxPool(10).props(ServerActor.props()), JarvisConstants.SERVER_AKKA_SYSTEM_NAME);

        int taskDispatcherThreads = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(taskDispatcherThreads);
        for (int i = 0; i < taskDispatcherThreads; i++) {
            executorService.submit(SpringContext.getBean(TaskDispatcher.class));
        }
        executorService.shutdown();

        TaskRetryScheduler taskRetryScheduler = TaskRetryScheduler.INSTANCE;
        taskRetryScheduler.start();

        init();

        LOGGER.info("Jarvis server started.");
    }

    public static void init() throws Exception {
        initScheduler();
        // initTimerTask();
    }

    private static void initScheduler() throws JobScheduleException {
        // 1. register schedulers to controller
        JobSchedulerController controller = JobSchedulerController.getInstance();
        DAGScheduler dagScheduler = SpringContext.getBean(DAGScheduler.class);
        TaskScheduler taskScheduler = SpringContext.getBean(TaskScheduler.class);
        AlarmScheduler alarmScheduler = SpringContext.getBean(AlarmScheduler.class);
        TimeScheduler timeScheduler = TimeSchedulerFactory.create();
        controller.register(dagScheduler);
        controller.register(taskScheduler);
        controller.register(timeScheduler);
        controller.register(alarmScheduler);

        // 2. initialize DAGScheduler and TimeScheduler
        JobService jobService = SpringContext.getBean(JobService.class);
        TaskService taskService = SpringContext.getBean(TaskService.class);
        List<Job> jobs = jobService.getNotDeletedJobs();
        for (Job job : jobs) {
            long jobId = job.getJobId();
            JobEntry jobEntry = jobService.get(jobId);
            Set<Long> dependencies = jobEntry.getDependencies().keySet();
            int cycleFlag = 0;
            int timeFlag = 0;
            List<ScheduleExpression> timeExpressions = jobEntry.getScheduleExpressions();
            if (!timeExpressions.isEmpty()) {
                for (ScheduleExpression expression : timeExpressions) {
                    if (expression instanceof CronExpression || expression instanceof FixedRateExpression
                            || expression instanceof ISO8601Expression) {
                        timeFlag = 1;
                    } else if (expression instanceof FixedDelayExpression) {
                        cycleFlag = 1;
                    }
                }
            }
            int dependFlag = (!dependencies.isEmpty()) ? 1 : 0;
            DAGJobType type = DAGJobType.getDAGJobType(timeFlag, dependFlag, cycleFlag);
            dagScheduler.getJobGraph().addJob(jobId, new DAGJob(jobId, type), dependencies);
            if (type.implies(DAGJobType.TIME)) {
                timeScheduler.addJob(jobId);
            }
        }

        // 3. initialize TaskScheduler
        List<Task> readyTasks = taskService.getTasksByStatus(Lists.newArrayList(JobStatus.WAITING.getValue(), JobStatus.READY.getValue()));
        List<Task> runningTasks = taskService.getTasksByStatus(JobStatus.RUNNING.getValue());
        taskScheduler.init(readyTasks, runningTasks);

        // 4. start schedulers
        controller.notify(new StartEvent());
    }
}
