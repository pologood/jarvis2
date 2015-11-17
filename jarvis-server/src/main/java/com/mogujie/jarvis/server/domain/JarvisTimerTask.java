/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年10月19日 上午10:40:23
 */

package com.mogujie.jarvis.server.domain;

import java.util.TimerTask;

import com.mogujie.jarvis.server.scheduler.controller.JobSchedulerController;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class JarvisTimerTask extends TimerTask {
    private JobService jobService = SpringContext.getBean(JobService.class);
    private JobSchedulerController schedulerController = JobSchedulerController.getInstance();

    @Override
    public void run() {
        //TODO
    }
}
