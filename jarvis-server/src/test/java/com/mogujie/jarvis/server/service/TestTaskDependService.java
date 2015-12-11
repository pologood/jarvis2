package com.mogujie.jarvis.server.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by muming on 15/11/17.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:context.xml")
public class TestTaskDependService {

    @Autowired
    private TaskDependService taskDependService;

    @Autowired IDService idService;

    @Test
    public void testStoreLoadParent() {

        long taskId = idService.getNextTaskId();
        Map<Long, List<Long>> write = new HashMap<>();
        write.put(100L, Arrays.asList(101L, 102L, 103L));
        write.put(200L, Arrays.asList(201L, 202L, 203L));
        write.put(30000L, Arrays.asList(30001L, 30002L, 30003L));
        taskDependService.storeParent(taskId, write);

        Map<Long, List<Long>> read = taskDependService.loadParent(taskId);

        Assert.assertEquals(write, read);

        taskDependService.remove(taskId);

    }
}
