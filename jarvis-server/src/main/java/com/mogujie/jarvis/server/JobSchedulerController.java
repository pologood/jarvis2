/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:35:46
 */

package com.mogujie.jarvis.server;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.eventbus.EventBus;
import com.mogujie.jarvis.server.observer.Event;
import com.mogujie.jarvis.server.observer.Observable;
import com.mogujie.jarvis.server.observer.Observer;
import com.mogujie.jarvis.server.scheduler.dag.DAGScheduler;
import com.mogujie.jarvis.server.scheduler.event.InitEvent;
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
@Service
public class JobSchedulerController implements Observable {
    @Autowired
    private TimeScheduler timeScheduler;

    @Autowired
    private DAGScheduler dagScheduler;

    @Autowired
    private TaskScheduler taskScheduler;

    private EventBus eventBus = new EventBus("JobSchedulerController");

    @PostConstruct
    public void init() throws Exception {
        register(timeScheduler);
        register(dagScheduler);
        register(taskScheduler);

        notify(new InitEvent());
    }

    @PreDestroy
    public void destroy() {
        unregister(timeScheduler);
        unregister(timeScheduler);
        unregister(timeScheduler);
    }

    @Override
    public void register(Observer o) {
        eventBus.register(o);
    }

    @Override
    public void unregister(Observer o) {
        eventBus.unregister(o);
    }

    @Override
    public void notify(Event event) {
        eventBus.post(event);
    }
}
