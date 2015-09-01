/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月1日 上午11:53:44
 */

package com.mogujie.jarvis.server.scheduler.dag.event;

/**
 * @author guangming
 *
 */
public class SuccessEvent extends DAGTaskEvent {

    /**
     * @param jobid
     * @param taskid
     */
    public SuccessEvent(long jobid, long taskid) {
        super(jobid, taskid);
    }

}
