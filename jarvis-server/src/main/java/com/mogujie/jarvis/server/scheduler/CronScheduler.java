/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月10日 下午4:22:47
 */

package com.mogujie.jarvis.server.scheduler;

import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.mogujie.jarvis.core.domain.Pair;
import com.mogujie.jarvis.dto.Crontab;
import com.mogujie.jarvis.server.cron.CronExpression;
import com.mogujie.jarvis.server.scheduler.controller.JobSchedulerController;
import com.mogujie.jarvis.server.scheduler.controller.SchedulerControllerFactory;
import com.mogujie.jarvis.server.scheduler.event.TimeReadyEvent;

@Repository
public class CronScheduler {

    private JobSchedulerController jobSchedulerController = SchedulerControllerFactory.getController();

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private SchedulerThread schedulerThread;
    private Map<Long, Crontab> crontabs = new ConcurrentHashMap<Long, Crontab>(10);
    private List<Pair<Long, DateTime>> nextScheduleTimeList = new Vector<Pair<Long, DateTime>>(10);
    private static final Logger LOGGER = LogManager.getLogger();

    public void start() {
        schedulerThread = new SchedulerThread(crontabs, nextScheduleTimeList);
        executor.execute(schedulerThread);
        executor.shutdown();
    }

    public void shutdown() {
        if (schedulerThread != null) {
            schedulerThread.shutdown();
        }
    }

    public void schedule(Crontab crontab) {
        crontabs.put(crontab.getJobId(), crontab);

        try {
            CronExpression expression = new CronExpression(crontab.getCronExpression());
            Pair<Long, DateTime> pair = new Pair<Long, DateTime>(crontab.getJobId(), expression.getTimeAfter(DateTime.now()));
            nextScheduleTimeList.add(pair);
        } catch (ParseException e) {
            LOGGER.error(e);
        }
    }

    public void scheduleOnce(long jobId, int delaySeconds) {
        DateTime dateTime = DateTime.now().plusSeconds(delaySeconds);
        Pair<Long, DateTime> pair = new Pair<Long, DateTime>(jobId, dateTime);
        nextScheduleTimeList.add(pair);
    }

    public void remove(Crontab crontab) {
        remove(crontab.getJobId());
    }

    public void remove(long jobId) {
        crontabs.remove(jobId);
        for (int i = 0, len = nextScheduleTimeList.size(); i < len; i++) {
            Pair<Long, DateTime> pair = nextScheduleTimeList.get(i);
            if (pair.getFirst().equals(jobId)) {
                nextScheduleTimeList.remove(i);
                break;
            }
        }
    }

    class SchedulerThread extends Thread {

        private Map<Long, Crontab> crontabs;
        private List<Pair<Long, DateTime>> nextScheduleTimeList;
        private volatile boolean running = true;

        public SchedulerThread(Map<Long, Crontab> crontabs, List<Pair<Long, DateTime>> nextScheduleTimeList) {
            this.crontabs = crontabs;
            this.nextScheduleTimeList = nextScheduleTimeList;
        }

        @Override
        public void run() {
            Comparator<Pair<Long, DateTime>> comparator = new Comparator<Pair<Long, DateTime>>() {
                public int compare(Pair<Long, DateTime> p1, Pair<Long, DateTime> p2) {
                    return p1.getSecond().compareTo(p2.getSecond());
                }
            };

            while (running) {
                if (nextScheduleTimeList.size() > 0) {
                    Collections.sort(nextScheduleTimeList, comparator);

                    Pair<Long, DateTime> pair = nextScheduleTimeList.get(0);
                    while (DateTime.now().compareTo(pair.getSecond()) >= 0) {
                        long jobId = pair.getFirst();
                        TimeReadyEvent event = new TimeReadyEvent(jobId);
                        jobSchedulerController.notify(event);

                        Crontab crontab = crontabs.get(jobId);
                        if (crontab != null) {
                            nextScheduleTimeList.remove(0);
                            try {
                                DateTime nextTime = new CronExpression(crontab.getCronExpression()).getTimeAfter(DateTime.now());
                                nextScheduleTimeList.add(new Pair<Long, DateTime>(pair.getFirst(), nextTime));
                            } catch (ParseException e) {
                                LOGGER.error(e);
                            }
                        }

                        if (nextScheduleTimeList.size() == 0) {
                            break;
                        }

                        pair = nextScheduleTimeList.get(0);
                    }
                }

                try {
                    Thread.sleep(1000 - System.currentTimeMillis() % 1000);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }

        public void shutdown() {
            running = false;
        }

    }
}
