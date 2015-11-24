/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年11月24日 下午4:24:40
 */

package com.mogujie.jarvis.server.scheduler;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joda.time.DateTime;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicLongMap;
import com.mogujie.jarvis.core.domain.IdType;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.observer.Event;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.core.util.ThreadUtils;
import com.mogujie.jarvis.server.TaskQueue;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;

/**
 * Task Retry Scheduler
 *
 */
public enum TaskRetryScheduler {
    INSTANCE;

    private TaskQueue taskQueue = TaskQueue.INSTANCE;
    private volatile boolean running;
    private Map<String, Pair<TaskDetail, Integer>> taskMap = Maps.newConcurrentMap();
    private AtomicLongMap<String> taskRetriedCounter = AtomicLongMap.create();
    private Comparator<Pair<String, DateTime>> comparator = new Comparator<Pair<String, DateTime>>() {

        @Override
        public int compare(Pair<String, DateTime> o1, Pair<String, DateTime> o2) {
            return o1.getSecond().compareTo(o2.getSecond());
        }
    };

    private SortedSet<Pair<String, DateTime>> taskSet = new ConcurrentSkipListSet<>(comparator);
    private JobSchedulerController schedulerController = JobSchedulerController.getInstance();

    public void start() {
        running = true;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new TaskRetryThread());
        executorService.shutdown();
    }

    public void addTask(TaskDetail taskDetail, int retries, int interval) {
        String fullId = taskDetail.getFullId();
        taskMap.put(fullId, new Pair<TaskDetail, Integer>(taskDetail, retries));
        taskSet.add(new Pair<String, DateTime>(fullId, DateTime.now().plusSeconds(interval)));
    }

    public void shutdown() {
        running = false;
    }

    class TaskRetryThread extends Thread {

        @Override
        public void run() {
            while (running) {
                DateTime now = DateTime.now();
                for (Pair<String, DateTime> pair : taskSet) {
                    if (pair.getSecond().isBefore(now)) {
                        String fullId = pair.getFirst();
                        TaskDetail taskDetail = taskMap.get(fullId).getFirst();
                        if (taskDetail != null) {
                            int retries = taskMap.get(fullId).getSecond();
                            if (taskRetriedCounter.get(fullId) > retries) {
                                taskMap.remove(fullId);
                                taskRetriedCounter.remove(fullId);

                                long jobId = IdUtils.parse(taskDetail.getFullId(), IdType.JOB_ID);
                                long taskId = IdUtils.parse(taskDetail.getFullId(), IdType.TASK_ID);
                                Event event = new FailedEvent(jobId, taskId);
                                schedulerController.notify(event);
                            } else {
                                taskQueue.put(taskDetail);
                                taskRetriedCounter.getAndIncrement(fullId);
                            }
                        }
                    } else {
                        break;
                    }
                }

                ThreadUtils.sleep(1000);
            }
        }
    }
}
