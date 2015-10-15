/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年10月15日 上午9:40:12
 */

package com.mogujie.jarvis.server.scheduler;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.mogujie.jarvis.dto.Crontab;
import com.mogujie.jarvis.server.scheduler.controller.JobSchedulerController;

/**
 * 
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TestCronScheduler {

    @Mock
    private JobSchedulerController controller;
    private CronScheduler scheduler;

    @Before
    public void setup() {
        scheduler = new CronScheduler(controller);
        scheduler.start();
    }

    @Test
    public void test() {
        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                System.out.println(new Date());
                return null;
            }
        }).when(controller).notify(any());

        Crontab crontab = new Crontab();
        crontab.setCronExpression("0/3 * * ? * *");
        crontab.setJobId(1L);
        scheduler.schedule(crontab);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void shutdown() {
        scheduler.shutdown();
    }
}
