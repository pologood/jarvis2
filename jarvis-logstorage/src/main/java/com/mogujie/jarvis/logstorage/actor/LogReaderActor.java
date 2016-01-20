/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: muming
 * Create Date: 2015年8月31日 下午4:10:00
 */

package com.mogujie.jarvis.logstorage.actor;

import akka.actor.Props;
import akka.actor.UntypedActor;
import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.logstorage.domain.LogReadResult;
import com.mogujie.jarvis.logstorage.logStream.LocalLogStream;
import com.mogujie.jarvis.logstorage.logStream.LogStream;
import com.mogujie.jarvis.protocol.LogProtos.LogStorageReadLogResponse;
import com.mogujie.jarvis.protocol.LogProtos.RestReadLogRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author 牧名
 */
public class LogReaderActor extends UntypedActor {

    private  static final Logger logger = LogManager.getLogger();
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

        LogStorageReadLogResponse response;
        try {
            //fullID
            String fullId = msg.getFullId();
            StreamType streamType = StreamType.parseValue(msg.getType());

            LogStream logStream = new LocalLogStream(fullId, streamType);

            //读取日志（本地）
            LogReadResult readResult = logStream.readText(msg.getOffset(), msg.getSize());

            //响应值_做成
            response = LogStorageReadLogResponse.newBuilder()
                    .setIsEnd(readResult.isEnd())
                    .setLog(readResult.getLog())
                    .setOffset(readResult.getOffset())
                    .setSuccess(true)
                    .build();

            //响应值_返回
            getSender().tell(response, getSelf());

        } catch (Exception e) {
            response = LogStorageReadLogResponse.newBuilder().setSuccess(false)
                    .setMessage(e.getMessage() != null ? e.getMessage() : e.toString()).build();
            getSender().tell(response, getSelf());
            logger.error("",e);
            throw e;
        }

    }


}
