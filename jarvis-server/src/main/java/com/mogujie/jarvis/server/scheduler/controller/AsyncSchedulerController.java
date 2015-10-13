/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月28日 下午8:08:18
 */

package com.mogujie.jarvis.server.scheduler.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.eventbus.AsyncEventBus;

/**
 * Async job scheduler controller
 *
 * @author guangming
 *
 */
public class AsyncSchedulerController extends JobSchedulerController {

    private AsyncSchedulerController() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        eventBus = new AsyncEventBus(executorService);
    }

    private static final AsyncSchedulerController single = new AsyncSchedulerController();

    public static AsyncSchedulerController getInstance() {
        return single;
    }
}
