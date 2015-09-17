/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月8日 下午1:48:22
 */

package com.mogujie.jarvis.server;

import org.springframework.stereotype.Service;

import com.mogujie.jarvis.core.domain.WorkerInfo;
import com.mogujie.jarvis.protocol.SubmitJobProtos.ServerSubmitTaskRequest;
import com.mogujie.jarvis.protocol.SubmitJobProtos.WorkerSubmitTaskResponse;

import akka.actor.ActorSelection;

@Service
public enum TaskDispatcher {

    INSTANCE;

    public static TaskDispatcher getInstance() {
        return INSTANCE;
    }

    public WorkerSubmitTaskResponse submit(ActorSelection actorSelection, ServerSubmitTaskRequest request, WorkerInfo workerInfo) {
        return null;
    }

}
