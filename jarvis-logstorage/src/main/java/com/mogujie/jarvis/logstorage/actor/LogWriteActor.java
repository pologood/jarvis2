/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午4:10:33
 */

package com.mogujie.jarvis.logstorage.actor;

import com.mogujie.jarvis.protocol.WriteLogProtos.WorkerWriteLogRequest;

import akka.actor.Props;
import akka.actor.UntypedActor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author wuya
 *
 */
public class LogWriteActor extends UntypedActor {

    public static Props props() {
        return Props.create(LogWriteActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {

        if (!(obj instanceof WorkerWriteLogRequest)) {
            unhandled(obj);
        }

        WorkerWriteLogRequest msg = (WorkerWriteLogRequest) obj;
        String path = LOG_DIR + "/" + msg.getJobId();
        if (msg.getType() == StreamType.STD_OUT.getValue()) {
            path += ".out";
        } else {
            path += ".err";
        }

        String tmpPath = path + ".tmp";
        String log = msg.getLog();
        if (!log.isEmpty()) {
            FileUtils.writeStringToFile(new File(tmpPath), msg.getLog() + SentinelConstants.LINE_SEPARATOR, StandardCharsets.UTF_8, true);
        }

        if (msg.getFinished() && new File(tmpPath).exists()) {
            FileUtils.moveFile(new File(tmpPath), new File(path));
        }


    }

}
