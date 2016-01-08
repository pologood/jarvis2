/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年11月24日 下午4:24:40
 */

package com.mogujie.jarvis.server.scheduler;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import akka.japi.tuple.Tuple3;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.AtomicLongMap;
import com.mogujie.jarvis.core.domain.IdType;
import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.observer.Event;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.core.util.ThreadUtils;
import com.mogujie.jarvis.server.ServerConigKeys;
import com.mogujie.jarvis.server.dispatcher.TaskQueue;
import com.mogujie.jarvis.server.domain.RetryType;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;

/**
 * Task Retry Scheduler
 *
 */
public enum TaskRetryScheduler {
    INSTANCE;

    private TaskQueue taskQueue = Injectors.getInjector().getInstance(TaskQueue.class);
    private volatile boolean running;
    private Map<Pair<String, RetryType>, TaskDetail> taskMap = Maps.newConcurrentMap();
    private Map<String, DateTime> expiredTimeMap = Maps.newConcurrentMap();
    private AtomicLongMap<String> taskFailedRetryCounter = AtomicLongMap.create();
    private int rejectInterval = ConfigUtils.getServerConfig().getInt(ServerConigKeys.TASK_REJECT_INTERVAL, 10);
    private Queue<Tuple3<String, RetryType, DateTime>> tasks = Queues.newLinkedBlockingQueue(100);
    private JobSchedulerController schedulerController = JobSchedulerController.getInstance();
    private static final Logger LOGGER = LogManager.getLogger();

    public void start() {
        running = true;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new TaskRetryThread());
        executorService.shutdown();
        LOGGER.info("Task retry scheduler started.");
    }

    public void addTask(TaskDetail taskDetail, RetryType retryType) {
        String jobIdWithTaskId = taskDetail.getFullId().replaceAll("_\\d+$", "");

        LOGGER.debug("add task to retry, jobIdWithTaskId: {}", jobIdWithTaskId);

        DateTime expiredDateTime = null;
        if (retryType == RetryType.FAILED_RETRY) {
            expiredDateTime = DateTime.now().plusSeconds(taskDetail.getFailedInterval());
            taskMap.putIfAbsent(new Pair<String, RetryType>(jobIdWithTaskId, retryType), taskDetail);
        } else {
            expiredDateTime = DateTime.now().plusSeconds(rejectInterval);
            if (!expiredTimeMap.containsKey(jobIdWithTaskId)) {
                taskMap.putIfAbsent(new Pair<String, RetryType>(jobIdWithTaskId, retryType), taskDetail);
                expiredTimeMap.putIfAbsent(jobIdWithTaskId, expiredDateTime);
            }
        }

        tasks.add(new Tuple3<String, RetryType, DateTime>(jobIdWithTaskId, retryType, expiredDateTime));
    }

    public void remove(String jobIdWithTaskId, RetryType retryType) {
        LOGGER.debug("remove retry task, jobIdWithTaskId: {}", jobIdWithTaskId);
        Pair<String, RetryType> taskKey = new Pair<String, RetryType>(jobIdWithTaskId, retryType);
        taskMap.remove(taskKey);
        taskFailedRetryCounter.remove(jobIdWithTaskId);
        Iterator<Tuple3<String, RetryType, DateTime>> it = tasks.iterator();
        while (it.hasNext()) {
            Tuple3<String, RetryType, DateTime> tuple3 = it.next();
            if (tuple3.t1().equals(jobIdWithTaskId) && tuple3.t2() == retryType) {
                it.remove();
            }
        }
    }

    public void shutdown() {
        running = false;
        LOGGER.info("Task retry scheduler shutdown");
    }

    class TaskRetryThread extends Thread {

        @Override
        public void run() {
            while (running) {
                try {
                    DateTime now = DateTime.now();
                    Iterator<Tuple3<String, RetryType, DateTime>> it = tasks.iterator();
                    while (it.hasNext()) {
                        Tuple3<String, RetryType, DateTime> taskKey = it.next();
                        if (taskKey.t3().isBefore(now)) {
                            String jobIdWithTaskId = taskKey.t1();
                            Pair<String, RetryType> pair = new Pair<String, RetryType>(jobIdWithTaskId, taskKey.t2());
                            TaskDetail taskDetail = taskMap.get(pair);
                            if (taskDetail == null) {
                                it.remove();
                                continue;
                            }

                            if (taskKey.t2() == RetryType.FAILED_RETRY) {
                                int retries = taskDetail.getFailedRetries();
                                if (taskFailedRetryCounter.get(jobIdWithTaskId) >= retries) {
                                    taskMap.remove(pair);
                                    taskFailedRetryCounter.remove(jobIdWithTaskId);
                                    long jobId = IdUtils.parse(taskDetail.getFullId(), IdType.JOB_ID);
                                    long taskId = IdUtils.parse(taskDetail.getFullId(), IdType.TASK_ID);
                                    Event event = new FailedEvent(jobId, taskId, "failed retry");
                                    schedulerController.notify(event);
                                } else {
                                    taskQueue.put(taskDetail);
                                    taskFailedRetryCounter.getAndIncrement(jobIdWithTaskId);
                                }
                            } else {
                                DateTime firstRetryTime = expiredTimeMap.get(jobIdWithTaskId);
                                int expiredTime = taskDetail.getExpiredTime();
                                if (expiredTime > 0 && firstRetryTime != null) {
                                    long timeDiff = (now.getMillis() - firstRetryTime.getMillis()) / 1000;
                                    if (timeDiff > expiredTime) {
                                        taskMap.remove(pair);
                                        expiredTimeMap.remove(jobIdWithTaskId);
                                        long jobId = IdUtils.parse(taskDetail.getFullId(), IdType.JOB_ID);
                                        long taskId = IdUtils.parse(taskDetail.getFullId(), IdType.TASK_ID);
                                        Event event = new FailedEvent(jobId, taskId, null);
                                        schedulerController.notify(event);
                                    } else {
                                        taskQueue.put(taskDetail);
                                    }
                                } else {
                                    taskQueue.put(taskDetail);
                                }
                            }
                            it.remove();
                        } else {
                            continue;
                        }
                    }

                    ThreadUtils.sleep(1000);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }

            LOGGER.info("TaskRetryThread exit");
        }

    }
}
