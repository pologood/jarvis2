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
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.inject.Singleton;
import com.mogujie.jarvis.core.domain.WorkerInfo;

@Singleton
public class HeartBeatService {

    private static final int MAX_HEART_BEAT_TIMEOUT_SECONDS = 15;
    private static final Map<Integer, Cache<WorkerInfo, Integer>> HEART_BEAT_CACHE = Maps.newConcurrentMap();
    private static final Ordering<WorkerInfo> WORKER_ORDERING = new Ordering<WorkerInfo>() {

        @Override
        public int compare(WorkerInfo left, WorkerInfo right) {
            if (left == null && right != null) {
                return -1;
            } else if (left != null && right == null) {
                return 1;
            } else if (left == null && right == null) {
                return 0;
            }

            String leftPath = left.getAkkaRootPath();
            String rightPath = right.getAkkaRootPath();
            if (leftPath == null && rightPath != null) {
                return -1;
            } else if (leftPath != null && rightPath == null) {
                return 1;
            } else if (leftPath == null && rightPath == null) {
                return 0;
            }

            return leftPath.compareTo(rightPath);
        }
    };

    public synchronized void put(int groupId, WorkerInfo workerInfo, final Integer jobNum) {
        Cache<WorkerInfo, Integer> cache = HEART_BEAT_CACHE.get(groupId);
        if (cache == null) {
            cache = CacheBuilder.newBuilder().expireAfterWrite(MAX_HEART_BEAT_TIMEOUT_SECONDS, TimeUnit.SECONDS).build();
            HEART_BEAT_CACHE.put(groupId, cache);
        }

        cache.put(workerInfo, jobNum);

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
