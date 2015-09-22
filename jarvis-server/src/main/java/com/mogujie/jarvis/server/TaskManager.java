/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月22日 下午2:45:50
 */

package com.mogujie.jarvis.server;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicLongMap;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.domain.WorkerInfo;

/**
 * 
 *
 */
@Repository
public class TaskManager {

    private Map<String, Pair<WorkerInfo, String>> taskMap = Maps.newHashMap();
    private ImmutableMap<String, Integer> maxParallelism = null;
    private AtomicLongMap<String> parallelismCounter = AtomicLongMap.create();

    @PostConstruct
    private void init() {
        Builder<String, Integer> builder = ImmutableMap.<String, Integer> builder();
        // TODO 应用最大并行度初始化

        maxParallelism = builder.build();
    }

    public synchronized boolean add(String fullId, WorkerInfo workerInfo, String appName) {
        taskMap.put(fullId, new Pair<>(workerInfo, appName));
        if (parallelismCounter.get(appName) >= maxParallelism.get(appName)) {
            return false;
        }

        parallelismCounter.getAndIncrement(appName);
        return true;
    }

    public synchronized WorkerInfo getWorkerInfo(String fullId) {
        return taskMap.get(fullId).getFirst();
    }

    public synchronized void remove(String fullId) {
        String appName = taskMap.get(fullId).getSecond();
        parallelismCounter.getAndDecrement(appName);
        taskMap.remove(fullId);
    }

    public synchronized boolean contains(String fullId) {
        return taskMap.containsKey(fullId);
    }
}
