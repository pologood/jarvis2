/*
 * 蘑菇街 Inc. Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming Create Date: 2015年12月2日 上午10:09:13
 */

package com.mogujie.jarvis.tasks;

import com.mogujie.jarvis.core.AbstractTask;
import com.mogujie.jarvis.core.TaskContext;
import com.mogujie.jarvis.core.exception.TaskException;

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
