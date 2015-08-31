/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 上午10:42:56
 */

package com.mogujie.jarvis.server.scheduler;

/**
 * @author guangming
 *
 */
public abstract class AbstractScheduler {

    /**
     * do initialized thing before run
     *
     */
    public abstract void init();

    /**
     * start the scheduler
     *
     */
    public abstract void run();

    /**
     * stop the scheduler
     *
     */
    public abstract void stop();
}
