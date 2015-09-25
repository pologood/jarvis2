/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:35:46
 */

package com.mogujie.jarvis.server;

import org.springframework.stereotype.Service;

import com.google.common.eventbus.EventBus;
import com.mogujie.jarvis.core.observer.Event;
import com.mogujie.jarvis.core.observer.Observable;
import com.mogujie.jarvis.core.observer.Observer;
import com.mogujie.jarvis.server.scheduler.Scheduler;

/**
 * Actor used to schedule job with three schedulers (
 * {@link com.mogujie.jarvis.server.scheduler.time.TimeScheduler},
 * {@link com.mogujie.jarvis.server.scheduler.dag.DAGScheduler}, and
 * {@link com.mogujie.jarvis.server.scheduler.task.TaskScheduler})
 *
 * @author guangming
 *
 */
@Service
public class JobSchedulerController implements Observable {
    private EventBus eventBus = new EventBus("JobSchedulerController");

    @Override
    public void register(Observer o) {
        if (o instanceof Scheduler) {
            eventBus.register(o);
        }
    }

    @Override
    public void unregister(Observer o) {
        if (o instanceof Scheduler) {
            eventBus.unregister(o);
        }
    }

    @Override
    public void notify(Event event) {
        eventBus.post(event);
    }
}
