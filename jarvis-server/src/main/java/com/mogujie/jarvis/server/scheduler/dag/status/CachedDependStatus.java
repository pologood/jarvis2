/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午1:52:49
 */

package com.mogujie.jarvis.server.scheduler.dag.status;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The implementation of AbstractDependStatus with cached map
 *
 * @author guangming
 *
 */
public class CachedDependStatus extends AbstractDependStatus {
    // Map<jobId, Map<taskId, status>>
    protected Map<Long, Map<Long, Boolean>> jobStatusMap =
            new ConcurrentHashMap<Long, Map<Long, Boolean>>();

    @Override
    public void setDependStatus(long jobId, long taskId) {
        modifyDependStatus(jobId, taskId, true);
    }

    @Override
    public void resetDependStatus(long jobId, long taskId) {
        modifyDependStatus(jobId, taskId, false);
    }

    private void modifyDependStatus(long jobId, long taskId, boolean status) {
        if (!jobStatusMap.containsKey(jobId)) {
            Map<Long, Boolean> taskStatusMap = new ConcurrentHashMap<Long, Boolean>();
            taskStatusMap.put(taskId, status);
            jobStatusMap.put(jobId, taskStatusMap);
        } else {
            Map<Long, Boolean> taskStatusMap = jobStatusMap.get(jobId);
            taskStatusMap.put(taskId, status);
        }
    }

    @Override
    public void removeDependency(long jobId) {
        if (jobStatusMap.containsKey(jobId)) {
            Map<Long, Boolean> taskStatusMap = jobStatusMap.get(jobId);
            if (taskStatusMap != null) {
                taskStatusMap.clear();
            }
            jobStatusMap.remove(jobId);
        }
    }

    @Override
    public void reset() {
        jobStatusMap.clear();
    }

    protected Map<Long, Map<Long, Boolean>> getJobStatusMap() {
        return this.jobStatusMap;
    }

}
