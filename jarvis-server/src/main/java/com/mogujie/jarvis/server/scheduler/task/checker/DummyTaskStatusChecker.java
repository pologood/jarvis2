/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月6日 下午2:09:42
 */

package com.mogujie.jarvis.server.scheduler.task.checker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mogujie.jarvis.core.domain.JobStatus;

/**
 * @author guangming
 *
 */
public class DummyTaskStatusChecker extends TaskStatusChecker {
    private Map<Long, JobStatus> parentsStatus;
    private List<Long> children;

    /**
     * @param jobId
     * @param taskId
     */
    public DummyTaskStatusChecker(long jobId, long taskId, Map<Long, Set<Long>> dependTaskIdMap) {
        super(jobId, taskId);
        parentsStatus = new HashMap<Long, JobStatus>();
        if (dependTaskIdMap != null) {
            for (Set<Long> taskSet : dependTaskIdMap.values()) {
                for (long preTaskId : taskSet) {
                    parentsStatus.put(preTaskId, JobStatus.READY);
                }
            }
        }
    }

    public void addChild(long taskId) {
        children.add(taskId);
    }

    @Override
    public boolean checkStatus() {
        boolean finishStatus = true;
        for (JobStatus status : parentsStatus.values()) {
            if (!status.equals(JobStatus.SUCCESS)) {
                finishStatus = false;
                break;
            }
        }
        return finishStatus;
    }

}
