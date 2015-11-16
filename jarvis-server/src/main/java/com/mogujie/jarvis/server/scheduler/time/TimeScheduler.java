/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年10月23日 上午11:32:26
 */

package com.mogujie.jarvis.server.scheduler.time;

import java.util.Iterator;
import java.util.SortedSet;

import org.joda.time.DateTime;

import com.mogujie.jarvis.server.scheduler.time.ExecutionPlan.ExecutionPlanEntry;

public class TimeScheduler extends Thread {

    private ExecutionPlan plan = ExecutionPlan.INSTANCE;
    private volatile boolean running = true;

    @Override
    public void run() {
        while (running) {
            DateTime now = DateTime.now();
            SortedSet<ExecutionPlanEntry> planSet = plan.getPlan();
            Iterator<ExecutionPlanEntry> it = planSet.iterator();
            while (it.hasNext()) {
                ExecutionPlanEntry entry = it.next();
                if (!entry.getDateTime().isAfter(now)) {
                    // TODO 调度任务
                    it.remove();
                } else {
                    break;
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void shutdown() {
        running = false;
    }
}
