/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午4:10:00
 */

package com.mogujie.jarvis.logstorage.actor;

import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.logstorage.domain.LogReadResult;
import com.mogujie.jarvis.logstorage.logStream.LocalLogStream;
import com.mogujie.jarvis.logstorage.logStream.LogStream;
import com.mogujie.jarvis.protocol.ReadLogProtos.LogServerReadLogResponse;
import com.mogujie.jarvis.protocol.ReadLogProtos.RestServerReadLogRequest;
import com.mogujie.jarvis.protocol.WriteLogProtos.LogServerWriteLogResponse;
import com.mogujie.jarvis.protocol.WriteLogProtos.WorkerWriteLogRequest;

import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * @author 牧名
 */
public class LogActor extends UntypedActor {

    public static Props props() {
        return Props.create(LogActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {

        if (obj instanceof RestServerReadLogRequest) {
            readLog((RestServerReadLogRequest) obj);

        } else if (obj instanceof WorkerWriteLogRequest) {
            writeLog((WorkerWriteLogRequest) obj);

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
    private void readLog(RestServerReadLogRequest msg) throws Exception {

        //fullID
        String fullId = msg.getFullId();
        StreamType streamType = StreamType.getInstance(msg.getType());

        LogStream logStream = new LocalLogStream(fullId,streamType);

        //读取日志（本地）
        LogReadResult readResult = logStream.readLines(msg.getOffset(), msg.getLines());

        //响应值_做成
        LogServerReadLogResponse response;
        response = LogServerReadLogResponse.newBuilder()
                .setIsEnd(readResult.isEnd())
                .setLog(readResult.getLog())
                .setOffset(readResult.getOffset())
                .setSuccess(true)
                .build();

        //响应值_返回
        getSender().tell(response, getSelf());

    }


    /**
     * 消息处理——写日志
     *
     * @param msg
     * @throws Exception
     */
    private void writeLog(WorkerWriteLogRequest msg) throws Exception {

        String fullId = msg.getFullId();
        StreamType streamType = StreamType.getInstance(msg.getType());
        String log = msg.getLog();
        Boolean isEnd = msg.getIsEnd();

        LogStream logStream = new LocalLogStream(fullId,streamType);


        //写log到本地文件
        logStream.writeLine(log);

        //log是否结束
        if (isEnd) {
            logStream.writeEndFlag();
        }

        //响应值_做成
        LogServerWriteLogResponse response;
        response = LogServerWriteLogResponse.newBuilder()
                .setSuccess(true)
                .build();

        //响应值_返回
        getSender().tell(response, getSelf());

    }


}
