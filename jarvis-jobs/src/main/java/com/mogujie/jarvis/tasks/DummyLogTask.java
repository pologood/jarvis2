/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年12月2日 上午10:09:13
 */

package com.mogujie.jarvis.tasks;

import com.mogujie.jarvis.core.TaskContext;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.exeception.TaskException;
import com.mogujie.jarvis.core.task.AbstractTask;

/**
 * @author muming
 *
 */
public class DummyLogTask extends AbstractTask {

    /**
     * @param taskContext
     */
    public DummyLogTask(TaskContext taskContext) {
        super(taskContext);
    }

    @Override
    public boolean execute() throws TaskException {
        TaskDetail task = getTaskContext().getTaskDetail();
        String stdoutLog = "outLog:DummyTask execute.";
        getTaskContext().getLogCollector().collectStdout(stdoutLog);
        String stderrLog = "errLog:DummyTask execute.";
        getTaskContext().getLogCollector().collectStdout(stderrLog);
        return true;
    }

    @Override
    public boolean kill() throws TaskException {
        return true;
    }

}
