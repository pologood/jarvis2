/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya Create Date: 2015年11月25日 下午1:59:14
 */

package com.mogujie.jarvis.server.scheduler;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.mogujie.jarvis.dao.generate.AlarmMapper;
import com.mogujie.jarvis.dao.generate.JobMapper;
import com.mogujie.jarvis.dto.generate.Alarm;
import com.mogujie.jarvis.dto.generate.AlarmExample;
import com.mogujie.jarvis.dto.generate.Job;
import com.mogujie.jarvis.server.alarm.AlarmType;
import com.mogujie.jarvis.server.alarm.Alarmer;
import com.mogujie.jarvis.server.alarm.DefaultAlarmer;
import com.mogujie.jarvis.server.scheduler.event.DAGTaskEvent;
import com.mogujie.jarvis.server.scheduler.event.FailedEvent;
import com.mogujie.jarvis.server.scheduler.event.KilledEvent;
import com.mogujie.jarvis.server.scheduler.event.StartEvent;
import com.mogujie.jarvis.server.scheduler.event.StopEvent;

@Repository
public class AlarmScheduler extends Scheduler {

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private AlarmMapper alarmMapper;

    private Alarmer alarmer = new DefaultAlarmer();

    private static final Logger LOGGER = LogManager.getLogger();

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
        long jobId = event.getJobId();
        Job job = jobMapper.selectByPrimaryKey(jobId);
        if (job != null) {
            String jobName = job.getJobName();
            AlarmExample alarmExample = new AlarmExample();
            alarmExample.createCriteria().andJobIdEqualTo(jobId);
            List<Alarm> alarms = alarmMapper.selectByExample(alarmExample);
            if (alarms != null && alarms.size() == 1) {
                Alarm alarm = alarms.get(0);
                if (alarm.getStatus().intValue() == 1) {
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
                        alarmTypes.add(AlarmType.getInstance(type));
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

}
