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
import com.mogujie.jarvis.core.util.ThreadUtils;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos.ServerRegistryResponse;
import com.mogujie.jarvis.protocol.RegistryWorkerProtos.WorkerRegistryRequest;
import com.mogujie.jarvis.worker.actor.DeadLetterActor;
import com.mogujie.jarvis.worker.actor.WorkerActor;
import com.mogujie.jarvis.worker.util.FutureUtils;
import com.typesafe.config.Config;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.DeadLetter;
import akka.routing.SmallestMailboxPool;
import scala.concurrent.duration.Duration;

public class JarvisWorker {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        Config akkaConfig = ConfigUtils.getAkkaConfig("akka-worker.conf");
        ActorSystem system = ActorSystem.create(JarvisConstants.WORKER_AKKA_SYSTEM_NAME, akkaConfig);

        Configuration workerConfig = ConfigUtils.getWorkerConfig();
        String serverAkkaPath = workerConfig.getString(WorkerConfigKeys.SERVER_AKKA_PATH) + JarvisConstants.SERVER_AKKA_USER_PATH;
        int workerGroupId = workerConfig.getInt(WorkerConfigKeys.WORKER_GROUP_ID, 0);
        String workerKey = workerConfig.getString(WorkerConfigKeys.WORKER_KEY);

        // 注册Worker：注册失败时退出，注册超时一直重试
        boolean register = false;
        while (!register) {
            WorkerRegistryRequest request = WorkerRegistryRequest.newBuilder().setKey(workerKey).build();
            ActorSelection serverActor = system.actorSelection(serverAkkaPath);
            try {
                ServerRegistryResponse response = (ServerRegistryResponse) FutureUtils.awaitResult(serverActor, request, 30);
                if (!response.getSuccess()) {
                    LOGGER.error("Worker register failed with group.id={}, worker.key={}, exit", workerGroupId, workerKey);
                    system.terminate();
                    return;
                } else {
                    LOGGER.info("Worker register successfully");
                    break;
                }
            } catch (Exception e) {
                LOGGER.error("Worker register timeout, waiting to retry...", e.getMessage());
            }

            ThreadUtils.sleep(workerConfig.getInt(WorkerConfigKeys.WORKER_REGISTRY_FAILED_INTERVAL, 5000));
        }

        ActorRef deadLetterActor = system.actorOf(new SmallestMailboxPool(10).props(DeadLetterActor.props()));
        system.eventStream().subscribe(deadLetterActor, DeadLetter.class);

        int actorNum = workerConfig.getInt(WorkerConfigKeys.WORKER_ACTORS_NUM, 500);
        ActorRef workerActor = system.actorOf(new SmallestMailboxPool(actorNum).props(WorkerActor.props()), JarvisConstants.WORKER_AKKA_SYSTEM_NAME);

        ActorSelection heartBeatActor = system.actorSelection(serverAkkaPath);

        int heartBeatInterval = workerConfig.getInt(WorkerConfigKeys.WORKER_HEART_BEAT_INTERVAL_SECONDS, 5);

        // 心跳汇报
        system.scheduler().schedule(Duration.Zero(), Duration.create(heartBeatInterval, TimeUnit.SECONDS),
                new HeartBeatThread(heartBeatActor, workerActor), system.dispatcher());

        LOGGER.info("Starting jarvis worker...");
    }

}
