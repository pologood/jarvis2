package com.mogujie.jarvis.server.service;

import com.mogujie.jarvis.dto.generate.Task;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * Created by muming on 15/11/17.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:context.xml")
public class TestTaskDependService {

    @Autowired
    private  TaskDependService taskDependService;

    @Test
    public void testStoreLoad(){

        long taskId = 1;
        Map<Long,List<Long>> write = new HashMap<>();
        write.put(100L, Arrays.asList(101L, 102L, 103L));
        write.put(200L, Arrays.asList(201L, 202L, 203L));
        write.put(30000L, Arrays.asList(30001L, 30002L, 30003L));
        taskDependService.store(taskId,write);

        Map<Long,List<Long>> read = taskDependService.load(taskId);

        Assert.assertEquals(write,read);

    }
}
