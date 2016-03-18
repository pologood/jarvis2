/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月22日 下午2:45:50
 */

package com.mogujie.jarvis.server.dispatcher;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicLongMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.domain.WorkerInfo;
import com.mogujie.jarvis.dto.generate.App;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.service.AppService;

@Singleton
public class TaskManager {

    private AppService appService = Injectors.getInjector().getInstance(AppService.class);

    private Map<String, Pair<WorkerInfo, Integer>> taskMap = Maps.newHashMap();
    private Map<Integer, Integer> maxParallelismMap = Maps.newHashMap();
    private AtomicLongMap<Integer> parallelismCounter = AtomicLongMap.create();
    private static final Logger LOGGER = LogManager.getLogger();

    @Inject
    private void init() {
        LOGGER.debug("init task manager");
        List<App> list = appService.getAppList();
        for (App app : list) {
            maxParallelismMap.put(app.getAppId(), app.getMaxConcurrency());
        }
    }

    public void addApp(int appId, int maxParallelism) {
        maxParallelismMap.put(appId, maxParallelism);
        LOGGER.info("add application: id[{}], parallelism[{}].", appId, maxParallelism);
    }

    public synchronized boolean addTask(String fullId, WorkerInfo workerInfo, int appId) {
        if (parallelismCounter.get(appId) >= maxParallelismMap.get(appId)) {
            return false;
        }

        parallelismCounter.getAndIncrement(appId);
        LOGGER.info("add task num, appId={}, num={}", appId, parallelismCounter.get(appId));

        taskMap.put(fullId, new Pair<>(workerInfo, appId));

        return true;
    }

    public synchronized WorkerInfo getWorkerInfo(String fullId) {
        return taskMap.get(fullId).getFirst();
    }

    public synchronized boolean contains(String fullId) {
        return taskMap.containsKey(fullId);
    }

    public void appCounterDecrement(int appId) {
        if (parallelismCounter.get(appId) > 0) {
            parallelismCounter.getAndDecrement(appId);
            LOGGER.info("reduce task num, appId={}, num={}", appId, parallelismCounter.get(appId));
        }
    }

    public void updateAppMaxParallelism(int appId, int maxParallelism) {
        maxParallelismMap.put(appId, maxParallelism);
    }

    public Map<Integer, Long> getAppCounter() {
        return parallelismCounter.asMap();
    }
}
