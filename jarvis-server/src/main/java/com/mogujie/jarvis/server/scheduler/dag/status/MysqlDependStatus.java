/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午1:29:59
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mogujie.jarvis.dto.JobDependStatus;
import com.mogujie.jarvis.dto.JobDependStatusKey;
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
    public void removeDependency(long jobId) {
        statusService.delDependencyByJobId(getMyJobId(), jobId);
    }

    @Override
    public void init() {
    }

    @Override
    public void reset() {
        statusService.clearMyStatus(getMyJobId());
    }

    @Override
    protected void modifyDependStatus(long jobId, long taskId, boolean status) {
        JobDependStatus record = MysqlDependStatusUtil.createDependStatus(
                getMyJobId(), jobId, taskId, status);
        JobDependStatusKey key = new JobDependStatusKey();
        key.setJobId(getMyJobId());
        key.setPreJobId(jobId);
        key.setPreTaskId(taskId);
        if (statusService.getByKey(key) != null) {
            statusService.update(record);
        } else {
            statusService.insert(record);
        }
    }

    @Override
    protected Map<Long, Map<Long, Boolean>> getJobStatusMap() {
        return MysqlDependStatusUtil.getJobStatusMapFromDb(statusService, getMyJobId());
    }
}
