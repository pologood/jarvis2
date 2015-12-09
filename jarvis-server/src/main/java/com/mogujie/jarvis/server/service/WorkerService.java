/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年11月27日 下午3:45:24
 */

package com.mogujie.jarvis.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import com.mogujie.jarvis.dao.generate.WorkerGroupMapper;
import com.mogujie.jarvis.dto.generate.WorkerGroup;
import com.mogujie.jarvis.dto.generate.WorkerGroupExample;
import org.joda.time.DateTime;
import com.mogujie.jarvis.dao.generate.WorkerMapper;
import com.mogujie.jarvis.dto.generate.Worker;
import com.mogujie.jarvis.dto.generate.WorkerExample;

/**
 * @author guangming
 */
@Service
public class WorkerService {
    @Autowired
    private WorkerMapper workerMapper;

    @Autowired
    private WorkerGroupMapper workerGroupMapper;

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

    public void saveWorker(String ip, int port, int groupId, int status) {
        Date dt = DateTime.now().toDate();
        Worker worker = new Worker();
        worker.setIp(ip);
        worker.setPort(port);
        worker.setWorkerGroupId(groupId);
        worker.setStatus(status);
        worker.setUpdateTime(dt);

        int workerId = getWorkerId(ip, port);
        if (workerId > 0) {
            worker.setId(workerId);
            workerMapper.updateByPrimaryKeySelective(worker);
        } else {
            worker.setCreateTime(dt);
            workerMapper.insert(worker);
        }
    }

    public int getGroupIdByAuthKey(String key) {
        WorkerGroupExample example = new WorkerGroupExample();
        example.createCriteria().andAuthKeyEqualTo(key);
        List<WorkerGroup> list = workerGroupMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return list.get(0).getId();
        }
        return 0;
    }

    public WorkerGroup getGroupByGroupId(int groupId) {
        return  workerGroupMapper.selectByPrimaryKey(groupId);
    }

}
