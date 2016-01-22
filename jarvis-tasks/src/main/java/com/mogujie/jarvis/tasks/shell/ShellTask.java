/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月9日 下午4:34:01
 */

package com.mogujie.jarvis.tasks.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mogujie.jarvis.core.AbstractTask;
import com.mogujie.jarvis.core.TaskContext;
import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.tasks.ShellStreamProcessor;
import com.mogujie.jarvis.tasks.util.ShellUtils;

/**
 * @author muming
 *
 */
public class ShellTask extends AbstractTask {

    private Process shellProcess = null;
    private static final String DEFAULT_STATUS_PATH = "/tmp/jarvis_shell_status";
    private static final String STATUS_PATH_KEY = "shell.status.data.dir";
    private static final String STATUS_PATH = ConfigUtils.getWorkerConfig().getString(STATUS_PATH_KEY, DEFAULT_STATUS_PATH);
    private static final Logger LOGGER = LogManager.getLogger();

    public ShellTask(TaskContext taskContext) {
        super(taskContext);
        File file = new File(STATUS_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public String getCommand() {
        return getTaskContext().getTaskDetail().getContent();
    }

    public void processStdOutputStream(InputStream inputStream) {
        String line = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            while ((line = br.readLine()) != null) {
                getTaskContext().getLogCollector().collectStdout(line);
            }
        } catch (IOException e) {
            LOGGER.error("error shellProcess stdout stream", e);
        }
    }

    public void processStdErrorStream(InputStream inputStream) {
        String line = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            while ((line = br.readLine()) != null) {
                getTaskContext().getLogCollector().collectStderr(line);
            }
        } catch (IOException e) {
            LOGGER.error("error shellProcess stderr stream", e);
        }
    }

    @Override
    public boolean execute() {
        TaskDetail task = getTaskContext().getTaskDetail();
        try {
            String statusFilePath = STATUS_PATH + "/" + task.getFullId() + ".status";
            StringBuilder sb = new StringBuilder();
            String cmd = getCommand();
            sb.append(cmd);
            if (!cmd.endsWith(";")) {
                sb.append(";");
            }

            sb.append("export SENTINEL_EXIT_CODE=$? ").append("&& echo $SENTINEL_EXIT_CODE > ").append(statusFilePath)
                    .append(" && exit $SENTINEL_EXIT_CODE");

            ProcessBuilder processBuilder = ShellUtils.createProcessBuilder(sb.toString());
            shellProcess = processBuilder.start();

            Thread stdoutStreamProcessor = new ShellStreamProcessor(this, shellProcess.getInputStream(), StreamType.STD_OUT);
            stdoutStreamProcessor.start();

            Thread stderrStreamProcessor = new ShellStreamProcessor(this, shellProcess.getErrorStream(), StreamType.STD_ERR);
            stderrStreamProcessor.start();

            boolean result = (shellProcess.waitFor() == 0);
            return result;
        } catch (Exception e) {
            getTaskContext().getLogCollector().collectStderr(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean kill() {
        if (shellProcess != null) {
            shellProcess.destroy();
        }
        return true;
    }

}
