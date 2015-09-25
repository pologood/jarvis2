/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月8日 上午10:26:00
 */

package com.mogujie.jarvis.server.scheduler;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.observer.Observer;
import com.mogujie.jarvis.server.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;

/**
 * @author guangming
 *
 */
@Repository
public abstract class Scheduler implements Observer {
    @Autowired
    private JobSchedulerController schedulerController;

    public void setSchedulerController(JobSchedulerController schedulerController) {
        this.schedulerController = schedulerController;
    }

    public JobSchedulerController getSchedulerController() {
        return schedulerController;
    }

    @PostConstruct
    public abstract void init();

    @PreDestroy
    public abstract void destroy();

    @Subscribe
    public abstract void handleStartEvent(StartEvent event);

    @Subscribe
    public abstract void handleStopEvent(StopEvent event);
}
