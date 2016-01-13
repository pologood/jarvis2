/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月21日 下午10:01:50
 */

package com.mogujie.jarvis.worker.actor;

import com.mogujie.jarvis.core.domain.TaskStatus;
import com.mogujie.jarvis.protocol.ReportTaskStatusProtos.WorkerReportTaskStatusRequest;

import akka.actor.DeadLetter;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * @author wuya
 *
 */
public class DeadLetterActor extends UntypedActor {

    public static Props props() {
        return Props.create(DeadLetterActor.class);
    }

    @Override
    public void onReceive(Object obj) throws Exception {
        if (obj instanceof DeadLetter) {
            DeadLetter deadLetter = (DeadLetter) obj;
            Object msg = deadLetter.message();
            if (msg instanceof WorkerReportTaskStatusRequest) {
                WorkerReportTaskStatusRequest status = (WorkerReportTaskStatusRequest) msg;

                if (status.getStatus() == TaskStatus.SUCCESS.getValue() || status.getStatus() == TaskStatus.FAILED.getValue()
                        || status.getStatus() == TaskStatus.KILLED.getValue()) {
                    getSender().tell(status, getSelf());
                    Thread.sleep(1000);
                }
            }
        } else {
            unhandled(obj);
        }
    }
}
