/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: muming
 * Create Date: 2015年12月1日 下午7:15:05
 */

package com.mogujie.jarvis.server.service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.mogujie.jarvis.core.util.JsonHelper;
import com.mogujie.jarvis.dao.generate.TaskDependMapper;
import com.mogujie.jarvis.dto.generate.TaskDepend;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author muming
 */
@Service
public class TaskDependService {

    @Autowired
    private TaskDependMapper mapper;

    Type dataType = new TypeToken<Map<Long, List<Long>>>() {}.getType();

    public void store(long taskId, Map<Long, List<Long>> dependTaskIdMap) {
        String dependJson = JsonHelper.toJson(dependTaskIdMap, dataType);
        TaskDepend newData = new TaskDepend();
        newData.setTaskId(taskId);
        newData.setDependTaskIds(dependJson);
        newData.setCreateTime(DateTime.now().toDate());

        TaskDepend oldData = mapper.selectByPrimaryKey(taskId);
        if (oldData == null) {
            mapper.insert(newData);
        } else {
            mapper.updateByPrimaryKey(newData);
        }
    }

    public Map<Long, List<Long>> load(long taskId) {
        TaskDepend data = mapper.selectByPrimaryKey(taskId);
        if (data == null) {
            return null;
        }
        return JsonHelper.fromJson(data.getDependTaskIds(), dataType);
    }

}
