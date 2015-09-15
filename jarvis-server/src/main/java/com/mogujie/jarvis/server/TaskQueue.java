/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月15日 下午4:40:30
 */

package com.mogujie.jarvis.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.mogujie.jarvis.core.Task;
import com.mogujie.jarvis.server.domain.TaskEvent;

/**
 * 
 *
 */
public enum TaskQueue {
    INSTANCE;

    private ExecutorService es = Executors.newCachedThreadPool();
    private Disruptor<TaskEvent> disruptor = new Disruptor<TaskEvent>(TaskEvent.EVENT_FACTORY, (int) Math.pow(2, 10), es, ProducerType.SINGLE,
            new BlockingWaitStrategy());

    public static TaskQueue getInstance() {
        return INSTANCE;
    }

    public void put(Task task) {

    }

}
