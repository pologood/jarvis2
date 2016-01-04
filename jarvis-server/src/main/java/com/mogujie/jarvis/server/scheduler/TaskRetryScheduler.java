/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年11月24日 下午4:24:40
 */

package com.mogujie.jarvis.server.scheduler;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import org.joda.time.DateTime;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicLongMap;
import com.mogujie.jarvis.core.domain.IdType;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.observer.Event;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.core.util.ThreadUtils;
import com.mogujie.jarvis.server.dispatcher.TaskQueue;
import com.mogujie.jarvis.server.domain.RetryType;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;

import akka.japi.tuple.Tuple3;

/**
 * Task Retry Scheduler
 *
 */
public enum TaskRetryScheduler {
    INSTANCE;

    private TaskQueue taskQueue = Injectors.getInjector().getInstance(TaskQueue.class);
    private volatile boolean running;
    private Map<Pair<String, RetryType>, Pair<TaskDetail, Integer>> taskMap = Maps.newConcurrentMap();
    private AtomicLongMap<Pair<String, RetryType>> taskRetriedCounter = AtomicLongMap.create();
    private Comparator<Tuple3<String, RetryType, DateTime>> comparator = new Comparator<Tuple3<String, RetryType, DateTime>>() {

        @Override
        public int compare(Tuple3<String, RetryType, DateTime> o1, Tuple3<String, RetryType, DateTime> o2) {
            return o1.t3().compareTo(o2.t3());
        }
    };

    private Queue<Tuple3<String, RetryType, DateTime>> tasks = new PriorityBlockingQueue<>(100, comparator);
    private JobSchedulerController schedulerController = JobSchedulerController.getInstance();

    public void start() {
        running = true;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new TaskRetryThread());
        executorService.shutdown();
    }

    public void addTask(TaskDetail taskDetail, int retries, int interval, RetryType retryType) {
        String jobIdWithTaskId = taskDetail.getFullId().replaceAll("_\\d+$", "");
        taskMap.put(new Pair<String, RetryType>(jobIdWithTaskId, retryType), new Pair<TaskDetail, Integer>(taskDetail, retries));
        tasks.add(new Tuple3<String, RetryType, DateTime>(jobIdWithTaskId, retryType, DateTime.now().plusSeconds(interval)));
    }

    public void remove(String jobIdWithTaskId, RetryType retryType) {
        Pair<String, RetryType> taskKey = new Pair<String, RetryType>(jobIdWithTaskId, retryType);
        taskMap.remove(taskKey);
        taskRetriedCounter.remove(taskKey);
    }

    public void shutdown() {
        running = false;
    }

    class TaskRetryThread extends Thread {

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            while (running) {
                DateTime now = DateTime.now();

                Object[] array = new Object[tasks.size()];
                Arrays.sort(tasks.toArray(array));
                for (Object obj : array) {
                    Tuple3<String, RetryType, DateTime> taskSetKey = (Tuple3<String, RetryType, DateTime>) obj;
                    if (taskSetKey.t3().isBefore(now)) {
                        Pair<String, RetryType> taskKey = new Pair<String, RetryType>(taskSetKey.t1(), taskSetKey.t2());
                        Pair<TaskDetail, Integer> taskValue = taskMap.get(taskKey);
                        TaskDetail taskDetail = taskValue.getFirst();
                        if (taskDetail != null) {
                            int retries = taskValue.getSecond();
                            if (taskRetriedCounter.get(taskKey) > retries) {
                                taskMap.remove(taskKey);
                                taskRetriedCounter.remove(taskKey);
                                long jobId = IdUtils.parse(taskDetail.getFullId(), IdType.JOB_ID);
                                long taskId = IdUtils.parse(taskDetail.getFullId(), IdType.TASK_ID);
                                Event event = new FailedEvent(jobId, taskId, null);
                                schedulerController.notify(event);
                            } else {
                                taskQueue.put(taskDetail);
                                taskRetriedCounter.getAndIncrement(taskKey);
                            }
                        }
                        tasks.remove(obj);
                    } else {
                        break;
                    }
                }

                ThreadUtils.sleep(1000);
            }
        }
    }
}
