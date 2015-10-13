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

    private Map<String, Pair<WorkerInfo, Integer>> taskMap = Maps.newHashMap();
    private Map<Integer, Integer> maxParallelismMap = Maps.newHashMap();
    private AtomicLongMap<Integer> parallelismCounter = AtomicLongMap.create();

    @PostConstruct
    private void init() {
        AppExample example = new AppExample();
        List<App> list = appMapper.selectByExample(example);
        for (App app : list) {
            maxParallelismMap.put(app.getAppId(), app.getMaxConcurrency());
        }
    }

    private Integer queryAppIdByName(String appName) {
        AppExample example = new AppExample();
        example.createCriteria().andAppNameEqualTo(appName);
        List<App> list = appMapper.selectByExample(example);
        if (list.size() > 0) {
            return list.get(0).getAppId();
        } else {
            return null;
        }
    }

    public synchronized boolean add(String fullId, WorkerInfo workerInfo, int appId) {
        taskMap.put(fullId, new Pair<>(workerInfo, appId));
        if (parallelismCounter.get(appId) >= maxParallelismMap.get(appId)) {
            return false;
        }

        parallelismCounter.getAndIncrement(appId);
        return true;
    }

    public synchronized WorkerInfo getWorkerInfo(String fullId) {
        return taskMap.get(fullId).getFirst();
    }

    public synchronized void remove(String fullId) {
        int appId = taskMap.get(fullId).getSecond();
        parallelismCounter.getAndDecrement(appId);
        taskMap.remove(fullId);
    }

    public synchronized boolean contains(String fullId) {
        return taskMap.containsKey(fullId);
    }

    public void appCounterDecrement(int appId) {
        if (parallelismCounter.get(appId) > 0) {
            parallelismCounter.getAndDecrement(appId);
        }
    }

    public void appCounterDecrement(String appName) {
        Integer appId = queryAppIdByName(appName);
        if (appId != null) {
            appCounterDecrement(appId);
        }
    }

    public void updateAppMaxParallelism(int appId, int maxParallelis) {
        maxParallelismMap.put(appId, maxParallelis);
    }
}
