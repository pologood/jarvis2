/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月27日 下午3:45:24
 */

package com.mogujie.jarvis.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.mogujie.jarvis.dao.generate.WorkerMapper;
import com.mogujie.jarvis.dto.generate.Worker;
import com.mogujie.jarvis.dto.generate.WorkerExample;

/**
 * @author guangming
 *
 */
public class WorkerService {
    @Autowired
    private WorkerMapper workerMapper;

    public int getWorkerId(String ip, int port) {
        int workerId = -1;
        WorkerExample example = new WorkerExample();
        example.createCriteria().andIpEqualTo(ip).andPortEqualTo(port);
        List<Worker> workers = workerMapper.selectByExample(example);
        if (workers != null && !workers.isEmpty()) {
            workerId = workers.get(0).getId();
        }
        return workerId;
    }
}
