/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午4:05:00
 */

package com.mogujie.jarvis.server;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mogujie.jarvis.core.domain.WorkerInfo;
import com.mogujie.jarvis.server.service.HeartBeatService;

/**
 * @author wuya
 *
 */
@Service
public class RandomWorkerSelector implements WorkerSelector {

    @Autowired
    private HeartBeatService heartBeatService;

    @Override
    public WorkerInfo select(int workerGroupId) {
        List<WorkerInfo> workers = heartBeatService.getWorkers(workerGroupId);
        if (workers != null && workers.size() > 0) {
            int min = 0;
            int max = workers.size() - 1;
            Random random = new Random();
            int index = random.nextInt(max) % (max - min + 1) + min;
            return workers.get(index);
        }

        return null;
    }

}
