/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月1日 下午3:01:04
 */

package com.mogujie.jarvis.worker;

import com.mogujie.jarvis.protocol.HeartBeatProtos.HeartBeatRequest;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;

/**
 * @author wuya
 *
 */
public class HeartBeatThread extends Thread {

  private ActorSelection heartBeatActor;
  private ActorRef sender;
  private TaskPool taskPool = TaskPool.INSTANCE;

  public HeartBeatThread(ActorSelection heartBeatActor, ActorRef sender) {
    this.heartBeatActor = heartBeatActor;
    this.sender = sender;
  }

  @Override
  public void run() {
    int jobNum = taskPool.size();
    HeartBeatRequest request = HeartBeatRequest.newBuilder().setJobNum(jobNum).build();
    heartBeatActor.tell(request, sender);
  }
}
