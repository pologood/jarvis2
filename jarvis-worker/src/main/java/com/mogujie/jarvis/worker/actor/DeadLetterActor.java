/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月21日 下午10:01:50
 */

package com.mogujie.jarvis.worker.actor;

import com.mogujie.jarvis.core.domain.JobStatus;
import com.mogujie.jarvis.protocol.ReportStatusProtos.WorkerReportStatusRequest;

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
      if (msg instanceof WorkerReportStatusRequest) {
        WorkerReportStatusRequest status = (WorkerReportStatusRequest) msg;

        // 只对成功或者失败的状态进行重发
        if (status.getStatus() == JobStatus.SUCCESS.getValue()
            || status.getStatus() == JobStatus.FAILED.getValue()) {
          getSender().tell(status, getSelf());
          Thread.sleep(3000);
        }
      }
    } else {
      unhandled(obj);
    }
  }
}
