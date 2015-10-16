/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年10月13日 上午10:43:20
 */

package com.mogujie.jarvis.server.scheduler.controller;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.util.ConfigUtils;

/**
 * @author guangming
 *
 */
public class SchedulerControllerFactory {
    public static final String SCHEDULER_CONTROLLER_KEY = "scheduler.controller.key";
    public static final String DEFAULT_SCHEDULER_CONTROLLER = AsyncSchedulerController.class.getName();

    public static JobSchedulerController getController() {

        JobSchedulerController controller;
        Configuration conf = ConfigUtils.getServerConfig();
        String className = conf.getString(SCHEDULER_CONTROLLER_KEY, DEFAULT_SCHEDULER_CONTROLLER);

        if (className.equalsIgnoreCase(SyncSchedulerController.class.getName())) {
            controller = SyncSchedulerController.getInstance();
        }else{
            controller = AsyncSchedulerController.getInstance(); //非“同步”场合，都是“异步”。
        }

        return controller;
    }
}
