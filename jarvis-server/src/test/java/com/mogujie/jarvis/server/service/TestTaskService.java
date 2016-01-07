package com.mogujie.jarvis.server.service;

import com.mogujie.jarvis.core.domain.TaskType;
import com.mogujie.jarvis.dto.generate.Task;
import com.mogujie.jarvis.server.guice.Injectors;
import org.joda.time.DateTime;
import org.junit.Test;

/**
 * Created by muming on 16/1/7.
 */
public class TestTaskService {

    private TaskService taskService = ServiceInjectors.getInjector().getInstance(TaskService.class);

    @Test
    public void test(){
        Task task = taskService.getLastTask(1, DateTime.now().getMillis(), TaskType.SCHEDULE);
    }


}
