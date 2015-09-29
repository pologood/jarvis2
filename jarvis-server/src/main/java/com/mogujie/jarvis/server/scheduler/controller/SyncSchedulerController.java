/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月28日 下午8:14:19
 */

package com.mogujie.jarvis.server.scheduler.controller;

import javax.inject.Named;

import org.springframework.stereotype.Service;

import com.google.common.eventbus.EventBus;

/**
 * Sync job scheduler controller
 *
 * @author guangming
 *
 */
@Service
@Named("SyncSchedulerController")
public class SyncSchedulerController extends JobSchedulerController {

    public SyncSchedulerController() {
        eventBus = new EventBus("SyncJobSchedulerController");
    }
}
