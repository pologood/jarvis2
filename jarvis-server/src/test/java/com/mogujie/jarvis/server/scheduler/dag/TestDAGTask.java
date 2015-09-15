/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月14日 下午3:01:35
 */

package com.mogujie.jarvis.server.scheduler.dag;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.mogujie.jarvis.server.scheduler.task.DAGTask;

/**
 * @author guangming
 *
 */
public class TestDAGTask {
    private Queue<DAGTask> priorityQueue;

    @Before
    public void setup() {
        priorityQueue =  new PriorityQueue<DAGTask>(10,
          new Comparator<DAGTask>() {
            @Override
            public int compare(DAGTask task1, DAGTask task2) {
                return task2.getPriority() - task1.getPriority();
            }
        });
    }

    @After
    public void tearDown() {
        priorityQueue.clear();
    }

    @Test
    public void testPriorityTask() {
        DAGTask task1 = new DAGTask(1, 1, 3);
        DAGTask task2 = new DAGTask(2, 2, 4);
        DAGTask task3 = new DAGTask(3, 3, 2);
        priorityQueue.offer(task1);
        priorityQueue.offer(task2);
        priorityQueue.offer(task3);
        Assert.assertEquals(3, priorityQueue.size());
        Assert.assertEquals(2, priorityQueue.peek().getJobId());
        while (!priorityQueue.isEmpty()) {
            DAGTask task = priorityQueue.poll();
            System.out.println(task.toString());
        }
        Assert.assertEquals(0, priorityQueue.size());
    }
}
