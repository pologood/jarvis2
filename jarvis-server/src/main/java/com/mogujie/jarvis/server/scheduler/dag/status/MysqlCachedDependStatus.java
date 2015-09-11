/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午2:13:48
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mogujie.jarvis.dto.JobDependStatus;
import com.mogujie.jarvis.server.service.DependStatusService;

/**
 * @author guangming
 *
 */
public class MysqlCachedDependStatus extends CachedDependStatus {
    private DependStatusService statusService;

    public MysqlCachedDependStatus() {
        ApplicationContext ac = new ClassPathXmlApplicationContext("context.xml");
        statusService = ac.getBean(DependStatusService.class);
    }

    @Override
    public void setDependStatus(long jobId, long taskId) {
        super.setDependStatus(jobId, taskId);
        flush2DB();
    }

    @Override
    public void resetDependStatus(long jobId, long taskId) {
        super.resetDependStatus(jobId, taskId);
        flush2DB();
    }

    @Override
    public void removeDependency(long jobId) {
        super.removeDependency(jobId);
        flush2DB();
    }

    @Override
    public void reset() {
        super.reset();
        flush2DB();
    }

    @Override
    protected Map<Long, Map<Long, Boolean>> loadJobDependStatus() {
        return MysqlDependStatusUtil.getJobStatusMapFromDb(statusService, getMyJobId());
    }

    private void flush2DB() {
        // 1. first clear
        statusService.clearMyStatus(getMyJobId());

        // 2. add all
        for (Map.Entry<Long, Map<Long, Boolean>> jobEntry : jobStatusMap.entrySet()) {
            long jobId = jobEntry.getKey();
            Map<Long, Boolean> taskStatusMap = jobEntry.getValue();
            for (Map.Entry<Long, Boolean> taskEntry : taskStatusMap.entrySet()) {
                long taskId = taskEntry.getKey();
                boolean status = taskEntry.getValue();
                JobDependStatus jobDependStatus = MysqlDependStatusUtil.createDependStatus(
                        getMyJobId(), jobId, taskId, status);
                statusService.insert(jobDependStatus);
            }
        }
    }
}
