/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午1:29:59
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.dto.JobDependStatus;
import com.mogujie.jarvis.server.service.DependStatusService;

/**
 * The implementation of AbstractDependStatus with mysql mapping
 *
 * @author guangming
 *
 */
public class MysqlDependStatus extends AbstractDependStatus {
    private DependStatusService statusService;

    public MysqlDependStatus() {
        ApplicationContext ac = new ClassPathXmlApplicationContext("context.xml");
        statusService = ac.getBean(DependStatusService.class);
    }

    @Override
    public void setDependStatus(long jobId, long taskId) {
        JobDependStatus jobDependStatus = createDependStatus(jobId, taskId, JobStatus.SUCCESS);
        statusService.insert(jobDependStatus);

    }

    @Override
    public void resetDependStatus(long jobId, long taskId) {
        JobDependStatus jobDependStatus = createDependStatus(jobId, taskId, JobStatus.FAILED);
        statusService.insert(jobDependStatus);
    }

    @Override
    public void removeDependency(long jobId) {
        statusService.delDependencyByJobId(getMyJobId(), jobId);
    }

    @Override
    public void reset() {
        statusService.clearMyStatus(getMyJobId());
    }

    protected Map<Long, Map<Long, Boolean>> getJobStatusMap() {
        Map<Long, Map<Long, Boolean>> jobStatusMap =
                new ConcurrentHashMap<Long, Map<Long, Boolean>>();
        List<JobDependStatus> jobDependStatusList = statusService.getRecordsByMyJobId(getMyJobId());
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

    private JobDependStatus createDependStatus(Long jobId, Long taskId, JobStatus jobStatus) {
        JobDependStatus jobDependStatus = new JobDependStatus();
        jobDependStatus.setJobId(getMyJobId());
        jobDependStatus.setPreJobId(jobId);
        jobDependStatus.setPreTaskId(taskId);
        jobDependStatus.setPreTaskStatus((byte)jobStatus.getValue());

        return jobDependStatus;
    }

}
