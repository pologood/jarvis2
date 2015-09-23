/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月8日 下午1:52:05
 */

package com.mogujie.jarvis.server.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.mogujie.jarvis.core.domain.WorkerInfo;

/**
 * 
 *
 */
@Service
public class HeartBeatService {

    @Autowired
    private WorkerService workerService;

    private static final int MAX_HEART_BEAT_TIMEOUT_SECONDS = 30;
    private static final Map<Integer, LoadingCache<WorkerInfo, Integer>> HEART_BEAT_CACHE = Maps.newConcurrentMap();
    private static final Ordering<WorkerInfo> WORKER_ORDERING = new Ordering<WorkerInfo>() {

        @Override
        public int compare(WorkerInfo left, WorkerInfo right) {
            return left.getAkkaRootPath().compareTo(right.getAkkaRootPath());
        }
    };

    public synchronized void put(int groupId, WorkerInfo workerInfo, final Integer jobNum) {
        // TODO
    }

    public synchronized void remove(int groupId, WorkerInfo workerInfo) {
        Cache<WorkerInfo, Integer> cache = HEART_BEAT_CACHE.get(groupId);
        if (cache != null) {
            cache.invalidate(workerInfo);
        }
    }

    public synchronized List<WorkerInfo> getWorkers(int groupId) {
        if (!HEART_BEAT_CACHE.containsKey(groupId)) {
            return null;
        }

        return WORKER_ORDERING.sortedCopy(HEART_BEAT_CACHE.get(groupId).asMap().keySet());
    }

}
