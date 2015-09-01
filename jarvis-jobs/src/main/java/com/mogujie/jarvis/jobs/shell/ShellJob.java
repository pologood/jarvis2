/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月9日 下午4:34:01
 */

package com.mogujie.jarvis.jobs.shell;

import com.mogujie.jarvis.core.JobContext;
import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.core.job.AbstractJob;


import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.mogujie.jarvis.core.common.util.ShellUtils;

import com.mogujie.jarvis.jobs.ShellStreamProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author wuya
 *
 */
public class ShellJob extends AbstractJob {

    private Process process;
    private static final String STATUS_PATH = ConfigUtils.getWorkerConfig().getString("worker.job.status.path");
    private static final Logger LOGGER = LogManager.getLogger();


    public ShellJob(JobContext jobContext, Process process) {
        super(jobContext);
        this.process = process;
    }

    public String getCommand() {
        return getJobContext().getCommand();
    }

    public void processStdOutputStream(InputStream inputStream) {
        String line = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            while ((line = br.readLine()) != null) {
                getJobContext().getLogCollector().collectStdout(line);
            }
        } catch (IOException e) {
            LOGGER.error("error process stdout stream", e);
        }
    }

    public void processStdErrorStream(InputStream inputStream) {
        String line = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            while ((line = br.readLine()) != null) {
                getJobContext().getLogCollector().collectStderr(line);
            }
        } catch (IOException e) {
            LOGGER.error("error process stderr stream", e);
        }
    }

    @Override
    public boolean execute() {
        try {
            String statusFilePath = STATUS_PATH + "/" + getJobContext().getJobId() + ".status";
            StringBuilder sb = new StringBuilder();
            String cmd = getCommand();
            sb.append(cmd);
            if (!cmd.endsWith(";")) {
                sb.append(";");
            }

            sb.append("export SENTINEL_EXIT_CODE=$? && ");
            sb.append("echo $SENTINEL_EXIT_CODE > ");
            sb.append(statusFilePath);
            sb.append(" && exit $SENTINEL_EXIT_CODE");

            ProcessBuilder processBuilder = ShellUtils.createProcessBuilder(sb.toString());
            process = processBuilder.start();

            Thread stdoutStreamProcessor = new ShellStreamProcessor(this, process.getInputStream(), StreamType.STD_OUT);
            stdoutStreamProcessor.start();

            Thread stderrStreamProcessor = new ShellStreamProcessor(this, process.getErrorStream(), StreamType.STD_ERR);
            stderrStreamProcessor.start();
            boolean result = process.waitFor() == 0;

            // 删除状态文件
            File statusFile = new File(statusFilePath);
            if (statusFile.exists()) {
                statusFile.delete();
            }

            return result;
        } catch (Exception e) {
            getJobContext().getLogCollector().collectStderr(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean kill() {
        if (process != null) {
            process.destroy();
        }

        return true;
    }

}
