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

import com.mogujie.jarvis.server.service.DependStatusService;

/**
 * The implementation of AbstractDependStatus with mysql mapping
 *
 * @author guangming
 *
 */
public class MysqlDependStatus extends RuntimeDependStatus {
    private DependStatusService statusService;

    public MysqlDependStatus() {
        ApplicationContext ac = new ClassPathXmlApplicationContext("context.xml");
        statusService = ac.getBean(DependStatusService.class);
    }

    @Override
    public void removeDependency() {
        statusService.deleteDependencyByPreJobId(getMyJobId(), getPreJobId());
    }

    @Override
    public void init() {
    }

    @Override
    public void reset() {
        statusService.clearPreStatus(getMyJobId(), getPreJobId());
    }

    @Override
    protected void modifyDependStatus(long taskId, boolean status) {
        MysqlDependStatusUtil.modifyDependStatus(getMyJobId(), getPreJobId(), taskId,
                status, statusService);
    }

    @Override
    protected Map<Long, Boolean> getTaskStatusMap() {
        return MysqlDependStatusUtil.getTaskStatusMapFromDb(statusService,
                getMyJobId(), getPreJobId());
    }
}
