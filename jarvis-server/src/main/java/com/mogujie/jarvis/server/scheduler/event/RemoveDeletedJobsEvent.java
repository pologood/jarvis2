/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年10月19日 上午11:42:17
 */

package com.mogujie.jarvis.server.scheduler.event;

import java.util.List;

import com.mogujie.jarvis.core.observer.Event;

/**
 * @author guangming
 *
 */
public class RemoveDeletedJobsEvent implements Event {

    private List<Long> deletedJobIds;

    public RemoveDeletedJobsEvent(List<Long> jobIds) {
        this.deletedJobIds = jobIds;
    }

    public List<Long> getDeletedJobIds() {
        return deletedJobIds;
    }

    public void setDeletedJobIds(List<Long> jobIds) {
        this.deletedJobIds = jobIds;
    }
}
