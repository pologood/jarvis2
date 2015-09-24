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
import com.mogujie.jarvis.logstorage.util.LogUtil;
import com.mogujie.jarvis.protocol.ReadLogProtos.LogServerReadLogResponse;
import com.mogujie.jarvis.protocol.ReadLogProtos.RestServerReadLogRequest;

import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * @author 牧名
 */
public class LogReadActor extends UntypedActor {

    public static Props props() {
        return Props.create(LogReadActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {

        if (!(obj instanceof RestServerReadLogRequest)) {
            unhandled(obj);
            return;
        }

        RestServerReadLogRequest msg = (RestServerReadLogRequest) obj;


        //fullID
        String fullId = msg.getFullId();

        //获取文件路径（本地）
        String filePath = LogUtil.getLogPath4Local(fullId, StreamType.getInstance(msg.getType()));

        //读取日志（本地）
        LogReadResult readResult = LogUtil.readLines4locale(filePath, msg.getOffset(), msg.getLines());

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


}
