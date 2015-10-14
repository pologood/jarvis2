/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月7日 上午10:13:42
 */

package com.mogujie.jarvis.worker;

import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos.ServerRegistryResponse;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos.WorkerRegistryRequest;
import com.mogujie.jarvis.worker.actor.DeadLetterActor;
import com.mogujie.jarvis.worker.actor.WorkerActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.DeadLetter;
import akka.pattern.Patterns;
import akka.routing.SmallestMailboxPool;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class JarvisWorker {

    private static final Timeout TIMEOUT = new Timeout(Duration.create(30, TimeUnit.SECONDS));
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        LOGGER.info("Starting jarvis worker...");

        Config akkaConfig = ConfigUtils.getAkkaConfigWithCommon("akka-worker.conf");
        System.out.println(akkaConfig.getInt("a.b.c"));
        ActorSystem system = ActorSystem.create(JarvisConstants.WORKER_AKKA_SYSTEM_NAME, akkaConfig);

        Configuration workerConfig = ConfigUtils.getWorkerConfig();
        String serverAkkaPath = workerConfig.getString("server.akka.path") + JarvisConstants.SERVER_AKKA_USER_PATH;
        int workerGroupId = workerConfig.getInt("worker.group.id", 0);
        String workerKey = workerConfig.getString("worker.key");
        WorkerRegistryRequest request = WorkerRegistryRequest.newBuilder().setKey(workerKey).build();

        // 注册Worker
        ActorSelection serverActor = system.actorSelection(serverAkkaPath);
        Future<Object> future = Patterns.ask(serverActor, request, TIMEOUT);
        try {
            ServerRegistryResponse response = (ServerRegistryResponse) Await.result(future,
                    TIMEOUT.duration());
            if (!response.getSuccess()) {
                LOGGER.error("Worker regist failed with group.id={}, worker.key={}", workerGroupId,
                        workerKey);
                system.terminate();
                return;
            }
        } catch (Exception e) {
            LOGGER.error("Worker regist failed", e);
            system.terminate();
            return;
        }

        ActorRef deadLetterActor = system
                .actorOf(new SmallestMailboxPool(10).props(DeadLetterActor.props()));
        system.eventStream().subscribe(deadLetterActor, DeadLetter.class);

        int actorNum = workerConfig.getInt("worker.actors.num", 100);
        ActorRef workerActor = system.actorOf(
                new SmallestMailboxPool(actorNum).props(WorkerActor.props()),
                JarvisConstants.WORKER_AKKA_SYSTEM_NAME);

        ActorSelection heartBeatActor = system.actorSelection(serverAkkaPath);

        int heartBeatInterval = workerConfig.getInt("worker.heart.beat.interval.seconds", 5);

        // 心跳汇报
        system.scheduler().schedule(Duration.Zero(),
                Duration.create(heartBeatInterval, TimeUnit.SECONDS),
                new HeartBeatThread(heartBeatActor, workerActor), system.dispatcher());

    }

}
