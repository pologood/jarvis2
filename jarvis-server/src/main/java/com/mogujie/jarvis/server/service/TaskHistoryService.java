/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2016年1月11日 下午2:45:06
 */

package com.mogujie.jarvis.server.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mogujie.jarvis.dao.generate.TaskHistoryMapper;
import com.mogujie.jarvis.dto.generate.TaskHistory;

/**
 * @author guangming
 *
 */
@Singleton
public class TaskHistoryService {

    @Inject
    private TaskHistoryMapper taskHistoryMapper;

    public void insert(TaskHistory record) {
        taskHistoryMapper.insert(record);
    }

    public void insertSelective(TaskHistory record) {
        taskHistoryMapper.insertSelective(record);
    }
}
