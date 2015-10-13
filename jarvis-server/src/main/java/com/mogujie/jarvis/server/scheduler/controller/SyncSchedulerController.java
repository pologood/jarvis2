/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月28日 下午8:14:19
 */

package com.mogujie.jarvis.server.scheduler.controller;

import com.google.common.eventbus.EventBus;

/**
 * Sync job scheduler controller
 *
 * @author guangming
 *
 */
public class SyncSchedulerController extends JobSchedulerController {

    private SyncSchedulerController() {
        eventBus = new EventBus("SyncJobSchedulerController");
    }

    private static final SyncSchedulerController single = new SyncSchedulerController();

    public static SyncSchedulerController getInstance() {
        return single;
    }
}
