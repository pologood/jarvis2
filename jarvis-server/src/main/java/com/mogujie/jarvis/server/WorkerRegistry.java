/*
 * 蘑菇街 Inc. Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya Create Date: 2015年9月22日 上午10:12:56
 */

package com.mogujie.jarvis.server;

import java.util.HashMap;
import java.util.Map;

import com.mogujie.jarvis.core.domain.WorkerInfo;

public enum WorkerRegistry {

    INSTANCE;

    private Map<WorkerInfo, Integer> map = new HashMap<WorkerInfo, Integer>();

    public static WorkerRegistry getInstance() {
        return INSTANCE;
    }

    public void put(WorkerInfo workerInfo, int groupId) {
        map.put(workerInfo, groupId);
    }

    public int getWorkerGroupId(WorkerInfo workerInfo) {
        if (map.containsKey(workerInfo)) {
            return map.get(workerInfo);
        } else {
            return -1;
        }
    }

}
