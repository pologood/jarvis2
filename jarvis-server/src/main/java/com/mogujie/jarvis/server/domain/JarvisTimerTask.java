/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年10月19日 上午10:40:23
 */

package com.mogujie.jarvis.server.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.server.scheduler.controller.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.controller.SchedulerControllerFactory;
import com.mogujie.jarvis.server.scheduler.event.ModifyJobFlagsEvent;
import com.mogujie.jarvis.server.service.JobService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class JarvisTimerTask extends TimerTask {
    private JobService jobService = SpringContext.getBean(JobService.class);
    private JobSchedulerController schedulerController = SchedulerControllerFactory.getController();

    @Override
    public void run() {
        List<Job> activeExpiredJobs = jobService.getActiveExpiredJobs();
        List<Long> deletedJobIds = new ArrayList<Long>();
        List<Long> expiredJobIds = new ArrayList<Long>();
        for (Job job : activeExpiredJobs) {
            if (job.getActiveEndDate().equals(job.getActiveStartDate())) {
                // delete temp jobs, update jobFlag to DELETED
                jobService.updateJobFlag(job, this.getClass().getSimpleName(), JobFlag.DELETED.getValue());
                deletedJobIds.add(job.getJobId());
            } else {
                // update jobFlag to EXPIRED
                jobService.updateJobFlag(job, this.getClass().getSimpleName(), JobFlag.EXPIRED.getValue());
                expiredJobIds.add(job.getJobId());
            }
        }
        schedulerController.notify(new ModifyJobFlagsEvent(deletedJobIds, JobFlag.DELETED));
        schedulerController.notify(new ModifyJobFlagsEvent(expiredJobIds, JobFlag.EXPIRED));
    }
}
