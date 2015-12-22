/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午4:10:00
 */

package com.mogujie.jarvis.logstorage.actor;

import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.logstorage.logStream.LocalLogStream;
import com.mogujie.jarvis.logstorage.logStream.LogStream;
import com.mogujie.jarvis.protocol.WriteLogProtos.WorkerWriteLogRequest;
import com.mogujie.jarvis.protocol.WriteLogProtos.LogStorageWriteLogResponse;


import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * @author 牧名
 */
public class LogWriterActor extends UntypedActor {

    public static Props props() {
        return Props.create(LogWriterActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof WorkerWriteLogRequest) {
            writeLog((WorkerWriteLogRequest) obj);
        } else {
            unhandled(obj);
        }
    }

    /**
     * 消息处理——写日志
     *
     * @param msg
     * @throws Exception
     */
    private void writeLog(WorkerWriteLogRequest msg) throws Exception {

        String fullId = msg.getFullId();
        StreamType streamType = StreamType.parseValue(msg.getType());
        String log = msg.getLog();
        Boolean isEnd = msg.getIsEnd();

        LogStream logStream = new LocalLogStream(fullId, streamType);

        //写log到本地文件
        logStream.writeLine(log);

        //log是否结束
        if (isEnd) {
            logStream.writeEndFlag();
        }

        //响应值_做成
        LogStorageWriteLogResponse response;
        response = LogStorageWriteLogResponse.newBuilder()
                .setSuccess(true)
                .build();

        //响应值_返回
        getSender().tell(response, getSelf());

    }

}
