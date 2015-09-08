/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午4:00:33
 */

package com.mogujie.jarvis.server;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.mogujie.jarvis.core.domain.WorkerInfo;
import com.mogujie.jarvis.server.service.HeartBeatService;

@Service
public class RoundRobinWorkerSelector implements WorkerSelector {

    @Autowired
    private HeartBeatService heartBeatService;

    private Map<Integer, Integer> map = Maps.newConcurrentMap();

    @Override
    public synchronized WorkerInfo select(int workerGroupId) {
        List<WorkerInfo> workers = heartBeatService.getWorkers(workerGroupId);
        if (workers != null && workers.size() > 0) {
            if (map.containsKey(workerGroupId)) {
                int index = map.get(workerGroupId) + 1;
                if (index >= workers.size()) {
                    index = 0;
                }

                map.put(workerGroupId, index);
                return workers.get(index);
            } else {
                map.put(workerGroupId, 0);
                return workers.get(0);
            }
        }

        return null;
    }

}
