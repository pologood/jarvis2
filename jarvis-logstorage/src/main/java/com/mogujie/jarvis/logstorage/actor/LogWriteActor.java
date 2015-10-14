/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年8月31日 下午4:10:33
 */

package com.mogujie.jarvis.logstorage.actor;

import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.logstorage.util.LogUtil;
import com.mogujie.jarvis.protocol.WriteLogProtos.LogServerWriteLogResponse;
import com.mogujie.jarvis.protocol.WriteLogProtos.WorkerWriteLogRequest;

import akka.actor.Props;
import akka.actor.UntypedActor;


/**
 * @author 牧名
 */
public class LogWriteActor extends UntypedActor {

    public static Props props() {
        return Props.create(LogWriteActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {

        if (obj instanceof WorkerWriteLogRequest) {
            dealWorkerWriteLogRequest((WorkerWriteLogRequest) obj);

        }else{
            unhandled(obj);
        }
    }


    /**
     * 消息处理——写日志
     *
     * @param msg
     * @throws Exception
     */
    private void dealWorkerWriteLogRequest(WorkerWriteLogRequest msg)throws Exception{

        String fullId = msg.getFullId();
        StreamType streamType = StreamType.getInstance(msg.getType());
        String log = msg.getLog();
        Boolean isEnd = msg.getIsEnd();

        //获取文件路径
        String filePath = LogUtil.getLogPath4Local(fullId, streamType);

        //写log到本地文件
        LogUtil.writeLine4Local(filePath, log);

        //log是否结束
        if (isEnd) {
            LogUtil.writeEndFlag2Local(filePath);
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
