/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:42:32
 */

package com.mogujie.jarvis.server.scheduler.time;

import com.mogujie.jarvis.server.observer.Observer;


/**
 * Scheduler used to handle time based job.
 *
 * @author guangming
 *
 */
public class TimeScheduler implements Observer {

    private static TimeScheduler instance = new TimeScheduler();
    private TimeScheduler (){}
    public static TimeScheduler getInstance() {
            return instance;
    }

    public void init() {
        // TODO Auto-generated method stub

    }

    public void run() {
        // TODO Auto-generated method stub

    }

    public void stop() {
        // TODO Auto-generated method stub

    }

}
