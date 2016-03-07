/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年3月7日 上午9:48:38
 */

package com.mogujie.jarvis.server.actor.hook;

import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobDependRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobRequest;
import com.mogujie.jarvis.protocol.JobProtos.RestModifyJobScheduleExpRequest;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.event.ModifyKpiEvent;
import com.mogujie.jarvis.server.service.JobService;

/**
 * @author guangming
 *
 */
public class KpiJobModifyHook implements JobPostHook {
    private JobSchedulerController controller = JobSchedulerController.getInstance();
    private JobService jobService = Injectors.getInjector().getInstance(JobService.class);

    public void execute(Object obj) {
        if (obj instanceof RestModifyJobRequest) {
            RestModifyJobRequest request = (RestModifyJobRequest) obj;
            if (request.hasContent()) {
                long jobId = request.getJobId();
                Job job = jobService.get(jobId).getJob();
                if (job != null) {
                    String msg = request.getUser() + "更新任务[" + job.getJobName() + "]内容";
                    controller.notify(new ModifyKpiEvent(jobId, msg));
                }
            }
        } else if (obj instanceof RestModifyJobScheduleExpRequest) {
            RestModifyJobScheduleExpRequest request = (RestModifyJobScheduleExpRequest) obj;
            long jobId = request.getJobId();
            Job job = jobService.get(jobId).getJob();
            if (job != null) {
                String msg = request.getUser() + "更新任务[" + job.getJobName() + "]调度表达式";
                controller.notify(new ModifyKpiEvent(jobId, msg));
            }
        } else if (obj instanceof RestModifyJobDependRequest) {
            RestModifyJobDependRequest request = (RestModifyJobDependRequest) obj;
            long jobId = request.getJobId();
            Job job = jobService.get(jobId).getJob();
            if (job != null) {
                String msg = request.getUser() + "更新任务[" + job.getJobName() + "]依赖关系";
                controller.notify(new ModifyKpiEvent(jobId, msg));
            }
        }
    }
}
