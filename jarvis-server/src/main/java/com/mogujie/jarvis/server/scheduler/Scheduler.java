/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月8日 上午10:26:00
 */

package com.mogujie.jarvis.server.scheduler;

import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.observer.Observer;
import com.mogujie.jarvis.server.scheduler.controller.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;

/**
 * @author guangming
 *
 */
public abstract class Scheduler implements Observer {
    private JobSchedulerController schedulerController;

    public void setSchedulerController(JobSchedulerController schedulerController) {
        this.schedulerController = schedulerController;
    }

    public JobSchedulerController getSchedulerController() {
        return schedulerController;
    }

    public abstract void init();

    public abstract void destroy();

    @Subscribe
    public abstract void handleStartEvent(StartEvent event);

    @Subscribe
    public abstract void handleStopEvent(StopEvent event);
}
