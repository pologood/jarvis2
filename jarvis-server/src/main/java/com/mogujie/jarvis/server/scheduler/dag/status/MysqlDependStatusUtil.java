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

import org.joda.time.DateTime;

import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.dto.JobDependStatus;
import com.mogujie.jarvis.dto.JobDependStatusKey;
import com.mogujie.jarvis.server.domain.JobKey;
import com.mogujie.jarvis.server.service.DependStatusService;

/**
 * @author guangming
 *
 */
public class MysqlDependStatusUtil {
    public static Map<Long, Boolean> getTaskStatusMapFromDb(DependStatusService statusService,
            JobKey myJobKey, JobKey preJobKey) {
        Map<Long, Boolean> taskStatusMap = new ConcurrentHashMap<Long, Boolean>();
        List<JobDependStatus> taskDependStatusList = statusService.getRecordsByPreJobKey(myJobKey, preJobKey);
        for (JobDependStatus dependStatus : taskDependStatusList) {
            boolean status = (dependStatus.getPreTaskStatus() == JobStatus.SUCCESS.getValue()) ? true : false;
            taskStatusMap.put(dependStatus.getPreTaskId(), status);
        }
        return taskStatusMap;
    }

    public static void modifyDependStatus(JobKey myJobKey, JobKey preJobKey, long preTaskId,
            boolean status, DependStatusService statusService) {
        JobDependStatusKey key = new JobDependStatusKey();
        key.setJobId(myJobKey.getJobId());
        key.setJobVersion(myJobKey.getVersion());
        key.setPreJobId(preJobKey.getJobId());
        key.setPreJobVersion(preJobKey.getVersion());
        key.setPreTaskId(preTaskId);
        JobDependStatus record = statusService.getByKey(key);
        if (record != null) {
            DateTime dt = DateTime.now();
            Date currentTime = dt.toDate();
            record.setUpdateTime(currentTime);
            statusService.update(record);
        } else {
            JobStatus jobStatus = status ? JobStatus.SUCCESS : JobStatus.FAILED;
            record = new JobDependStatus();
            record.setJobId(myJobKey.getJobId());
            record.setJobVersion(myJobKey.getVersion());
            record.setPreJobId(preJobKey.getJobId());
            record.setPreJobVersion(preJobKey.getVersion());
            record.setPreTaskId(preTaskId);
            record.setPreTaskStatus(jobStatus.getValue());
            Date currentTime = new Date();
            DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
            dateTimeFormat.format(currentTime);
            record.setCreateTime(currentTime);
            record.setUpdateTime(currentTime);
            statusService.insert(record);
        }
    }
}
