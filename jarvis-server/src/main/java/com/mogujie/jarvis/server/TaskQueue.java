/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月15日 下午4:40:30
 */

package com.mogujie.jarvis.server;

import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;

import com.mogujie.jarvis.core.domain.TaskDetail;

public enum TaskQueue {

    INSTANCE;

    private Comparator<TaskDetail> comparator = new Comparator<TaskDetail>() {

        @Override
        public int compare(TaskDetail t1, TaskDetail t2) {
            return t2.getPriority() - t1.getPriority();
        }
    };

    private PriorityBlockingQueue<TaskDetail> queue = new PriorityBlockingQueue<>(100, comparator);

    public void put(TaskDetail taskDetail) {
        queue.put(taskDetail);
    }

    public TaskDetail take() throws InterruptedException {
        return queue.take();
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void clear() {
        queue.clear();
    }

    public Iterator<TaskDetail> iterator() {
        return queue.iterator();
    }

}
