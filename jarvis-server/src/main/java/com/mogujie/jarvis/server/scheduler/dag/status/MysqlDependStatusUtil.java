/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月11日 上午10:09:18
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.dto.JobDependStatus;
import com.mogujie.jarvis.dto.JobDependStatusKey;
import com.mogujie.jarvis.server.service.DependStatusService;

/**
 * @author guangming
 *
 */
public class MysqlDependStatusUtil {
    public static Map<Long, Boolean> getTaskStatusMapFromDb(DependStatusService statusService,
            long myJobId, long preJobId) {
        Map<Long, Boolean> taskStatusMap = new ConcurrentHashMap<Long, Boolean>();
        List<JobDependStatus> taskDependStatusList = statusService.getRecordsByPreJobId(myJobId, preJobId);
        for (JobDependStatus dependStatus : taskDependStatusList) {
            boolean status = (dependStatus.getPreTaskStatus() == JobStatus.SUCCESS.getValue()) ? true : false;
            taskStatusMap.put(dependStatus.getPreTaskId(), status);
        }
        return taskStatusMap;
    }

    public static JobDependStatus createDependStatus(Long myJobId, long jobId, long taskId, boolean status) {
        JobStatus jobStatus = status ? JobStatus.SUCCESS : JobStatus.FAILED;
        JobDependStatus jobDependStatus = new JobDependStatus();
        jobDependStatus.setJobId(myJobId);
        jobDependStatus.setPreJobId(jobId);
        jobDependStatus.setPreTaskId(taskId);
        jobDependStatus.setPreTaskStatus(jobStatus.getValue());

        return jobDependStatus;
    }

    public static void modifyDependStatus(long myJobId, long preJobId, long preTaskId,
            boolean status, DependStatusService statusService) {
        JobDependStatus record = createDependStatus(myJobId, preJobId, preTaskId, status);
        JobDependStatusKey key = new JobDependStatusKey();
        key.setJobId(myJobId);
        key.setPreJobId(preJobId);
        key.setPreTaskId(preTaskId);
        if (statusService.getByKey(key) != null) {
            Date currentTime = new Date();
            DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
            dateTimeFormat.format(currentTime);
            record.setUpdateTime(currentTime);
            statusService.update(record);
        } else {
            Date currentTime = new Date();
            DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
            dateTimeFormat.format(currentTime);
            record.setCreateTime(currentTime);
            record.setUpdateTime(currentTime);
            statusService.insert(record);
        }
    }
}
