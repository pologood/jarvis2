/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月1日 下午2:02:12
 */

package com.mogujie.jarvis.server.scheduler.dag.event;


/**
 * @author guangming
 *
 */
public class FailedEvent extends DAGTaskEvent {

    /**
     * @param jobid
     * @param taskid
     * @param jobContext
     */
    public FailedEvent(long jobid, long taskid) {
        super(jobid, taskid);
    }

}
