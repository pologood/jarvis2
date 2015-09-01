/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月9日 下午1:30:43
 */

package com.mogujie.jarvis.jobs;

import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.jobs.shell.ShellJob;

import java.io.InputStream;

/**
 * @author wuya
 *
 */
public class ShellStreamProcessor extends Thread {

    private ShellJob shellJob;
    private InputStream inputStream;
    private StreamType type;

    public ShellStreamProcessor(ShellJob job, InputStream inputStream, StreamType type) {
        this.shellJob = job;
        this.inputStream = inputStream;
        this.type = type;
    }

    @Override
    public void run() {
        if (type == StreamType.STD_OUT) {
            shellJob.processStdOutputStream(inputStream);
        } else if (type == StreamType.STD_ERR) {
            shellJob.processStdErrorStream(inputStream);
        }
    }
}
