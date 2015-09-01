/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月1日 下午2:26:16
 */

package com.mogujie.jarvis.core;

import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.protocol.WriteLogProtos.WorkerWriteLogRequest;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;

/**
 * @author wuya
 *
 */
public class DefaultLogCollector extends AbstractLogCollector {

    private ActorSelection logActor;
    private String fullId;
    private static final String LOGSERVER_AKKA_PATH = ConfigUtils.getWorkerConfig().getString("logserver.akka.path") + "/user/"
            + JarvisConstants.LOGSERVER_AKKA_PATH;

    public DefaultLogCollector(ActorSystem system, String fullId) {
        logActor = system.actorSelection(LOGSERVER_AKKA_PATH);
        this.fullId = fullId;
    }

    @Override
    public void collectStdout(String line, boolean isEnd) {
        WorkerWriteLogRequest request = WorkerWriteLogRequest.newBuilder().setFullId(fullId).setType(StreamType.STD_OUT.getValue()).setLog(line)
                .setIsEnd(isEnd).build();
        logActor.tell(request, ActorRef.noSender());
    }

    @Override
    public void collectStderr(String line, boolean isEnd) {
        WorkerWriteLogRequest request = WorkerWriteLogRequest.newBuilder().setFullId(fullId).setType(StreamType.STD_ERR.getValue()).setLog(line)
                .setIsEnd(isEnd).build();
        logActor.tell(request, ActorRef.noSender());
    }

}
