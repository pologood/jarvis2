/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月11日 上午10:09:18
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.dto.JobDependStatus;
import com.mogujie.jarvis.server.service.DependStatusService;

/**
 * @author guangming
 *
 */
public class MysqlDependStatusUtil {
    public static Map<Long, Map<Long, Boolean>> getJobStatusMapFromDb(DependStatusService statusService, long myJobId) {
        Map<Long, Map<Long, Boolean>> jobStatusMap =
                new ConcurrentHashMap<Long, Map<Long, Boolean>>();
        List<JobDependStatus> jobDependStatusList = statusService.getRecordsByMyJobId(myJobId);
        for (JobDependStatus dependStatus : jobDependStatusList) {
            long jobId = dependStatus.getPreJobId();
            long taskId = dependStatus.getPreTaskId();
            boolean status = (dependStatus.getPreTaskStatus() == JobStatus.SUCCESS.getValue()) ? true : false;
            if (!jobStatusMap.containsKey(jobId)) {
                Map<Long, Boolean> taskStatusMap = new ConcurrentHashMap<Long, Boolean>();
                taskStatusMap.put(taskId, status);
                jobStatusMap.put(jobId, taskStatusMap);
            } else {
                Map<Long, Boolean> taskStatusMap = jobStatusMap.get(jobId);
                taskStatusMap.put(taskId, status);
            }
        }
        return jobStatusMap;
    }

    public static JobDependStatus createDependStatus(Long myJobId, long jobId, long taskId, boolean status) {
        JobStatus jobStatus = status ? JobStatus.SUCCESS : JobStatus.FAILED;
        JobDependStatus jobDependStatus = new JobDependStatus();
        jobDependStatus.setJobId(myJobId);
        jobDependStatus.setPreJobId(jobId);
        jobDependStatus.setPreTaskId(taskId);
        jobDependStatus.setPreTaskStatus((byte)jobStatus.getValue());

        return jobDependStatus;
    }
}
