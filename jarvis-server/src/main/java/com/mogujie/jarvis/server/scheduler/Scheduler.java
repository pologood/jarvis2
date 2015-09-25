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

import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.observer.Observer;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;

/**
 * @author guangming
 *
 */
public interface Scheduler extends Observer {

    @PostConstruct
    public void init();

    @PreDestroy
    public void destroy();

    @Subscribe
    public void handleStartEvent(StartEvent event);

    @Subscribe
    public void handleStopEvent(StopEvent event);
}
