/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月1日 下午1:55:51
 */

package com.mogujie.jarvis.server.scheduler.event;

/**
 * @author guangming
 *
 */
public class TimeReadyEvent extends DAGJobEvent {

    /**
     * @param jobId
     */
    public TimeReadyEvent(long jobId) {
        super(jobId);
    }

}
