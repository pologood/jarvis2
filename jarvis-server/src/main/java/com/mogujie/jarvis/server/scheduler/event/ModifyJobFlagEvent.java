/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月2日 上午11:56:46
 */

package com.mogujie.jarvis.server.scheduler.event;

import com.mogujie.jarvis.core.domain.JobFlag;

/**
 * @author guangming
 *
 */
public class ModifyJobFlagEvent extends DAGJobEvent {
    private JobFlag jobFlag;

    /**
     * @param jobId
     */
    public ModifyJobFlagEvent(long jobId, JobFlag jobFlag) {
        super(jobId);
        this.jobFlag = jobFlag;
    }

    public JobFlag getJobFlag() {
        return jobFlag;
    }

    public void setJobFlag(JobFlag jobFlag) {
        this.jobFlag = jobFlag;
    }
}
