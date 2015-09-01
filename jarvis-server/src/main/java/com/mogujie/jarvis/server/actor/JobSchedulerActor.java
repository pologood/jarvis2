/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:35:46
 */

package com.mogujie.jarvis.server.actor;

import javax.inject.Named;

import org.springframework.context.annotation.Scope;

import akka.actor.UntypedActor;

import com.mogujie.jarvis.server.scheduler.dag.DAGScheduler;
import com.mogujie.jarvis.server.scheduler.task.TaskScheduler;
import com.mogujie.jarvis.server.scheduler.time.TimeScheduler;

/**
 * Actor used to schedule job with three schedulers (
 * {@link com.mogujie.jarvis.server.scheduler.time.TimeScheduler},
 * {@link com.mogujie.jarvis.server.scheduler.dag.DAGScheduler}, and
 * {@link com.mogujie.jarvis.server.scheduler.task.TaskScheduler})
 *
 * @author guangming
 *
 */
@Named("JobSchedulerActor")
@Scope("prototype")
public class JobSchedulerActor extends UntypedActor {

    private TimeScheduler timeScheduler = TimeScheduler.INSTANCE;
    private DAGScheduler dagScheduler = DAGScheduler.INSTANCE;
    private TaskScheduler taskScheduler = TaskScheduler.INSTANCE;

    @Override
    public void preStart() throws Exception {
        // init scheduler
        timeScheduler.init();
        dagScheduler.init();
        taskScheduler.init();

        // run scheduler
        timeScheduler.run();
        dagScheduler.run();
        taskScheduler.run();
    }

    @Override
    public void preRestart(Throwable reason, scala.Option<Object> message) throws Exception {

    }

    @Override
    public void onReceive(Object arg0) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void postStop() throws Exception {
        // stop scheduler
        timeScheduler.stop();
        dagScheduler.stop();
        taskScheduler.stop();
    }

}
