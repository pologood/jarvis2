/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:42:32
 */

package com.mogujie.jarvis.server.scheduler.time;

import com.mogujie.jarvis.server.scheduler.InitEvent;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.StopEvent;


/**
 * Scheduler used to handle time based job.
 *
 * @author guangming
 *
 */
public class TimeScheduler implements Scheduler {

    private static TimeScheduler instance = new TimeScheduler();
    private TimeScheduler() {}
    public static TimeScheduler getInstance() {
        return instance;
    }

    @Override
    public void handleInitEvent(InitEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleStopEvent(StopEvent event) {
        // TODO Auto-generated method stub

    }

}
