/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月1日 下午7:15:05
 */

package com.mogujie.jarvis.server.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * @author guangming
 *
 */
@Service
public class TaskDependService {

    public void store(long taskId, Map<Long, List<Long>> dependTaskIdMap) {
        //TODO
    }

    public Map<Long, List<Long>> load(long taskId) {
        //TODO
        return null;
    }

}
