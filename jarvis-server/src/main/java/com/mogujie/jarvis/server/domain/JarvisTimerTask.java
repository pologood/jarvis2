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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mogujie.jarvis.core.domain.JobFlag;
import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.server.scheduler.controller.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.controller.SchedulerControllerFactory;
import com.mogujie.jarvis.server.scheduler.event.RemoveDeletedJobsEvent;
import com.mogujie.jarvis.server.service.JobService;

/**
 * @author guangming
 *
 */
@Service
public class JarvisTimerTask extends TimerTask {
    @Autowired
    private JobService jobService;

    private JobSchedulerController schedulerController = SchedulerControllerFactory.getController();

    @Override
    public void run() {
        List<Job> activeExpiredJobs = jobService.getActiveExpiredJobs();
        List<Long> deleteJobIds = new ArrayList<Long>();
        for (Job job : activeExpiredJobs) {
            if (job.getActiveEndDate().equals(job.getActiveStartDate())) {
                // delete temp jobs, update jobFlag to DELETED
                jobService.updateJobFlag(job, this.getClass().getSimpleName(), JobFlag.DELETED.getValue());
                deleteJobIds.add(job.getJobId());
            } else {
                // update jobFlag to EXPIRED
                jobService.updateJobFlag(job, this.getClass().getSimpleName(), JobFlag.EXPIRED.getValue());
            }
        }
        schedulerController.notify(new RemoveDeletedJobsEvent(deleteJobIds));
    }
}
