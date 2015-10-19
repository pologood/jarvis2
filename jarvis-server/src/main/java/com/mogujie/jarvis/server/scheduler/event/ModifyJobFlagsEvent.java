/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年10月19日 上午11:42:17
 */

package com.mogujie.jarvis.server.scheduler.event;

import java.util.List;

import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.core.observer.Event;

/**
 * @author guangming
 *
 */
public class ModifyJobFlagsEvent implements Event {

    private List<Long> jobIds;
    private JobFlag newFlag;

    public ModifyJobFlagsEvent(List<Long> jobIds, JobFlag newFlag) {
        this.jobIds = jobIds;
        this.newFlag = newFlag;
    }

    public List<Long> getJobIds() {
        return jobIds;
    }

    public void setJobIds(List<Long> jobIds) {
        this.jobIds = jobIds;
    }

    public JobFlag getNewFlag() {
        return newFlag;
    }

    public void setNewFlag(JobFlag newFlag) {
        this.newFlag = newFlag;
    }
}
