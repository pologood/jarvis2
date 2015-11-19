package com.mogujie.jarvis.server.service;

import com.mogujie.jarvis.dto.Job;
import com.mogujie.jarvis.dto.Task;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by muming on 15/11/17.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:context.xml")
public class TestTaskService {

    @Autowired
    private  TaskService taskService;

    @Test
    public void testCreateTaskByJobId(){


        Integer jobId = 3;
        Long scheduleTime = DateTime.now().getMillis()/1000;

        Task task = taskService.createTaskByJobId(jobId,scheduleTime);

        Assert.assertNotNull(task);

    }



}
