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
    protected void modifyDependStatus(long taskId, boolean status) {
        super.modifyDependStatus(taskId, status);
        MysqlDependStatusUtil.modifyDependStatus(getMyJobId(), getPreJobId(),
                taskId, status, statusService);
    }

    @Override
    public void removeDependency() {
        super.removeDependency();
        statusService.deleteDependencyByPreJobId(getMyJobId(), getPreJobId());
    }

    @Override
    public void reset() {
        super.reset();
        statusService.clearMyStatus(getMyJobId());
    }

    @Override
    protected Map<Long, Boolean> loadTaskDependStatus() {
        return MysqlDependStatusUtil.getTaskStatusMapFromDb(
                statusService, getMyJobId(), getPreJobId());
    }
}
