/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2016 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2016年1月5日 下午3:24:52
 */

package com.mogujie.jarvis.server.scheduler;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.domain.TaskDetail.TaskDetailBuilder;
import com.mogujie.jarvis.core.util.ThreadUtils;
import com.mogujie.jarvis.server.dispatcher.TaskQueue;
import com.mogujie.jarvis.server.domain.RetryType;
import com.mogujie.jarvis.server.guice.Injectors;

public class TestTaskRetryScheduler {

    private TaskRetryScheduler taskRetryScheduler = TaskRetryScheduler.INSTANCE;
    private TaskQueue taskQueue = Injectors.getInjector().getInstance(TaskQueue.class);
    private TaskDetail taskDetail = null;

    @Before
    public void setup() {
        taskRetryScheduler.start();

        TaskDetailBuilder builder = TaskDetail.newTaskDetailBuilder();
        builder.setAppName("testApp");
        builder.setContent("content");
        builder.setDataTime(new DateTime(2016, 1, 5, 1, 2, 3));
        builder.setExpiredTime(10);
        builder.setFailedInterval(3);
        builder.setFailedRetries(3);
        builder.setFullId("123_456_789");
        builder.setGroupId(1);
        builder.setParameters(Maps.newHashMap());
        builder.setPriority(5);
        builder.setTaskName("testTask");
        builder.setTaskType("shell");
        builder.setUser("user");
        taskDetail = builder.build();
    }

    @Test
    public void testFailedRetry() {
        taskRetryScheduler.addTask(taskDetail, RetryType.FAILED_RETRY);
        taskRetryScheduler.addTask(taskDetail, RetryType.FAILED_RETRY);
        taskRetryScheduler.addTask(taskDetail, RetryType.FAILED_RETRY);
        taskRetryScheduler.addTask(taskDetail, RetryType.FAILED_RETRY);
        for (int i = 0; i < 10; i++) {
            System.out.println(taskQueue.size());
            ThreadUtils.sleep(1000);
        }

        System.out.println(taskQueue);
    }

    @After
    public void close() {
        taskRetryScheduler.shutdown();
    }
}
