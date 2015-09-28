/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月22日 下午2:45:50
 */

package com.mogujie.jarvis.server;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicLongMap;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.domain.WorkerInfo;
import com.mogujie.jarvis.dao.AppMapper;
import com.mogujie.jarvis.dto.App;
import com.mogujie.jarvis.dto.AppExample;

/**
 * 
 *
 */
@Repository
public class TaskManager {

    @Autowired
    private AppMapper appMapper;

    private Map<String, Pair<WorkerInfo, String>> taskMap = Maps.newHashMap();
    private Map<String, Integer> maxParallelismMap = null;
    private AtomicLongMap<String> parallelismCounter = AtomicLongMap.create();

    @PostConstruct
    private void init() {
        AppExample example = new AppExample();
        List<App> list = appMapper.selectByExample(example);
        for (App app : list) {
            maxParallelismMap.put(app.getAppName(), app.getMaxConcurrency());
        }
    }

    public synchronized boolean add(String fullId, WorkerInfo workerInfo, String appName) {
        taskMap.put(fullId, new Pair<>(workerInfo, appName));
        if (parallelismCounter.get(appName) >= maxParallelismMap.get(appName)) {
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

    public void appCounterDecrement(String appName) {
        if (parallelismCounter.get(appName) > 0) {
            parallelismCounter.getAndDecrement(appName);
        }
    }
}
