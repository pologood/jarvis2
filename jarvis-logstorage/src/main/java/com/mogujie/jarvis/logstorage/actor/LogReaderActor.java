/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午4:10:00
 */

package com.mogujie.jarvis.logstorage.actor;

import akka.actor.Props;
import akka.actor.UntypedActor;
import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.logstorage.domain.LogReadResult;
import com.mogujie.jarvis.logstorage.logStream.LocalLogStream;
import com.mogujie.jarvis.logstorage.logStream.LogStream;
import com.mogujie.jarvis.protocol.ReadLogProtos.LogStorageReadLogResponse;
import com.mogujie.jarvis.protocol.ReadLogProtos.RestReadLogRequest;

/**
 * @author 牧名
 */
public class LogReaderActor extends UntypedActor {

    public static Props props() {
        return Props.create(LogReaderActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof RestReadLogRequest) {
            readLog((RestReadLogRequest) obj);
        } else {
            unhandled(obj);
        }
    }

    /**
     * 处理消息——读取日志
     *
     * @param msg
     * @throws Exception
     */
    private void readLog(RestReadLogRequest msg) throws Exception {

        //fullID
        String fullId = msg.getFullId();
        StreamType streamType = StreamType.getInstance(msg.getType());

        LogStream logStream = new LocalLogStream(fullId,streamType);

        //读取日志（本地）
        LogReadResult readResult = logStream.readLines(msg.getOffset(), msg.getLines());

        //响应值_做成
        LogStorageReadLogResponse response;
        response = LogStorageReadLogResponse.newBuilder()
                .setIsEnd(readResult.isEnd())
                .setLog(readResult.getLog())
                .setOffset(readResult.getOffset())
                .setSuccess(true)
                .build();

        //响应值_返回
        getSender().tell(response, getSelf());

    }


}
