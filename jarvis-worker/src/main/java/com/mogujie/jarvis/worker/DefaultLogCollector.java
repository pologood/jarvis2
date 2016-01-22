/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月1日 下午2:26:16
 */

package com.mogujie.jarvis.worker;

import java.nio.charset.StandardCharsets;

import akka.actor.ActorRef;
import com.google.protobuf.ByteString;
import com.mogujie.jarvis.core.AbstractLogCollector;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.protocol.LogProtos.WorkerWriteLogRequest;
import com.mogujie.jarvis.protocol.LogProtos.LogStorageWriteLogResponse;

import akka.actor.ActorSelection;
import com.mogujie.jarvis.worker.util.FutureUtils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * @author wuya
 */
public class DefaultLogCollector extends AbstractLogCollector {

    private static Logger logger = LogManager.getLogger();

    private ActorSelection actor;
    private ActorRef sender;
    private String fullId;
    private int maxBytes = ConfigUtils.getWorkerConfig().getInt(WorkerConfigKeys.LOG_SEND_MAX_BYTES, 1024 * 1024);

    public DefaultLogCollector(ActorSelection actor, ActorRef sender, String fullId) {
        this.actor = actor;
        this.sender = sender;
        this.fullId = fullId;
    }

    private void sendLog(String line, boolean isEnd, StreamType streamType) {
        byte[] bytes = (line + JarvisConstants.LINE_SEPARATOR).getBytes(StandardCharsets.UTF_8);
        int srcLen = bytes.length;
        int i = 0;
        boolean sendEnd = false;
        while ((srcLen - maxBytes * i) > 0) {
            int needSize = maxBytes;
            if ((srcLen - maxBytes * (i + 1)) < 0) {
                needSize = srcLen - maxBytes * i;
                if (isEnd) {
                    sendEnd = true;
                }
            }

            byte[] dest = new byte[needSize];
            System.arraycopy(bytes, maxBytes * i, dest, 0, needSize);

            WorkerWriteLogRequest request = WorkerWriteLogRequest.newBuilder().setFullId(fullId).setType(streamType.getValue())
                    .setLog(ByteString.copyFrom(dest)).setIsEnd(sendEnd).build();
            LogStorageWriteLogResponse response;
            try {
//                actor.tell(request, sender);
                response = (LogStorageWriteLogResponse) FutureUtils.awaitResult(actor, request, 10);
                if (response.getSuccess()){
                    i++;
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    @Override
    public void collectStdout(String line, boolean isEnd) {
        sendLog(line, isEnd, StreamType.STD_OUT);
    }

    @Override
    public void collectStderr(String line, boolean isEnd) {
        sendLog(line, isEnd, StreamType.STD_ERR);
    }

}
