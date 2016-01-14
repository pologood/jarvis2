/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya Create Date: 2015年11月25日 下午1:59:14
 */

package com.mogujie.jarvis.server.alarm;

import java.util.List;

import com.mogujie.jarvis.core.domain.AlarmType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.dto.generate.Alarm;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.server.ServerConigKeys;
import com.mogujie.jarvis.server.guice.Injectors;
import com.mogujie.jarvis.server.scheduler.Scheduler;
import com.mogujie.jarvis.server.scheduler.event.DAGTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.KilledEvent;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;
import com.mogujie.jarvis.server.service.AlarmService;
import com.mogujie.jarvis.server.service.JobService;

/**
 * Manage alarm when task executed failed.
 */
public class AlarmScheduler extends Scheduler {

    private JobService jobService = Injectors.getInjector().getInstance(JobService.class);
    private AlarmService alarmService = Injectors.getInjector().getInstance(AlarmService.class);

    private static Alarmer alarmer = null;
    private static final Logger LOGGER = LogManager.getLogger();

    static {
        String alarmerClass = ConfigUtils.getServerConfig().getString(ServerConigKeys.ALARMER_CLASS);
        try {
            alarmer = (Alarmer) Class.forName(alarmerClass).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            Throwables.propagate(e);
        }
    }

    @Override
    public void handleStartEvent(StartEvent event) {

    }

    @Override
    public void handleStopEvent(StopEvent event) {

    }

    @Subscribe
    public void handleFailedEvent(FailedEvent event) {
        alarm(event);
    }

    @Subscribe
    public void handleKilledEvent(KilledEvent event) {
        alarm(event);
    }

    private void alarm(DAGTaskEvent event) {
        if (alarmer == null) {
            return;
        }

        long jobId = event.getJobId();
        Job job = jobService.get(jobId).getJob();
        if (job != null) {
            String jobName = job.getJobName();
            Alarm alarm = alarmService.getAlarmByJobId(jobId);
            if (alarm != null && alarm.getStatus().intValue() == 1) {
                String msg = null;
                if (event instanceof FailedEvent) {
                    msg = "任务[" + jobName + "]运行失败";
                } else if (event instanceof KilledEvent) {
                    msg = "任务[" + jobName + "]被Kill";
                }

                String[] tokens = alarm.getAlarmType().split(",");
                List<AlarmType> alarmTypes = Lists.newArrayList();
                for (String str : tokens) {
                    int type = Integer.parseInt(str);
                    alarmTypes.add(AlarmType.parseValue(type));
                }

                List<String> receiver = Lists.newArrayList(alarm.getReceiver().split(","));
                boolean success = alarmer.alarm(alarmTypes, receiver, msg);
                if (!success) {
                    LOGGER.warn("Alarm Failed");
                }
            }
        }
    }

}
