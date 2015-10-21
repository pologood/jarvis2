/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午2:13:48
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import java.util.Map;

import com.mogujie.jarvis.server.service.DependStatusService;
import com.mogujie.jarvis.server.util.SpringContext;

/**
 * @author guangming
 *
 */
public class MysqlCachedDependStatus extends CachedDependStatus {
    private DependStatusService statusService;

    public MysqlCachedDependStatus() {
        statusService = SpringContext.getBean(DependStatusService.class);
    }

    @Override
    protected void modifyDependStatus(long taskId, boolean status) {
        super.modifyDependStatus(taskId, status);
        MysqlDependStatusUtil.modifyDependStatus(getMyJobKey(), getPreJobKey(), taskId, status, statusService);
    }

    @Override
    public void reset() {
        super.reset();
        statusService.clearMyStatusByJobKey(getMyJobKey());
    }

    @Override
    protected Map<Long, Boolean> loadTaskDependStatus() {
        return MysqlDependStatusUtil.getTaskStatusMapFromDb(statusService, getMyJobKey(), getPreJobKey());
    }
}
