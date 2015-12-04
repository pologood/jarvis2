/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月9日 下午4:34:01
 */

package com.mogujie.jarvis.tasks.mock;

import com.mogujie.jarvis.core.TaskContext;
import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.task.AbstractTask;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.tasks.ShellStreamProcessor;
import com.mogujie.jarvis.tasks.util.ShellUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author muming
 *
 */
public class MockTask extends AbstractTask {

    private static final Logger LOGGER = LogManager.getLogger();

    public MockTask(TaskContext taskContext) {
        super(taskContext);
    }

    @Override
    public boolean execute() {
        TaskDetail task = getTaskContext().getTaskDetail();
        String stdoutLog = "outLog";
        getTaskContext().getLogCollector().collectStdout(stdoutLog);
        String stderrLog = "errLog";
        getTaskContext().getLogCollector().collectStdout(stderrLog);
        return true;
    }

    @Override
    public boolean kill() {
        return true;
    }

}
