/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月28日 下午2:04:01
 */

package com.mogujie.jarvis.tasks.shell;

import java.util.Set;

import com.mogujie.jarvis.core.TaskContext;
import com.mogujie.jarvis.core.domain.TaskDetail;

/**
 * @author guangming
 *
 */
public class HiveSqlShellTask extends HiveShellTask {

    /**
     * @param jobContext
     * @param applicationIdSet
     */
    public HiveSqlShellTask(TaskContext jobContext, Set<String> applicationIdSet) {
        super(jobContext, applicationIdSet);
    }

    @Override
    protected String getContent(TaskDetail task) {
        return task.getContent();
    }

}
