/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月2日 上午11:56:46
 */

package com.mogujie.jarvis.server.scheduler.event;

/**
 * @author guangming
 *
 */
public class RemoveJobEvent extends DAGJobEvent {

    /**
     * @param long jobId
     */
    public RemoveJobEvent(long jobId) {
        super(jobId);
    }

}
