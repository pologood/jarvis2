/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月6日 下午2:36:57
 */

package com.mogujie.jarvis.server.scheduler.task.checker;

import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.util.ConfigUtils;

/**
 * @author guangming
 *
 */
public class TaskStatusCheckerFactory {
    public static final String TASK_STATUS_CHECKER_KEY = "task.status.checker";
    public static final String DUMMY_TASK_STATUS_CHECKER = DummyTaskStatusChecker.class.getName();
    public static final String DEFAULT_TASK_STATUS_CHECKER = DefaultTaskStatusChecker.class.getName();

    public static TaskStatusChecker create(long myJobId, long myTaskId, Map<Long, Set<Long>> dependTaskIdMap) {
        TaskStatusChecker taskStatusChecker = null;
        Configuration conf = ConfigUtils.getServerConfig();
        String className = conf.getString(TASK_STATUS_CHECKER_KEY, DEFAULT_TASK_STATUS_CHECKER);
        if (className.equalsIgnoreCase(DUMMY_TASK_STATUS_CHECKER)) {
            taskStatusChecker = new DummyTaskStatusChecker(myJobId, myTaskId, dependTaskIdMap);
        } else {
            taskStatusChecker = new DefaultTaskStatusChecker(myJobId, myTaskId, dependTaskIdMap);
        }

        return taskStatusChecker;
    }
}
