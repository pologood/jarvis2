/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: muming
 * Create Date: 2015年9月1日 下午3:01:04
 */

package com.mogujie.jarvis.worker;

import akka.actor.ActorSelection;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.protocol.LogProtos.WorkerHeartBeatRequest;
import com.mogujie.jarvis.protocol.LogProtos.LogStorageHeartBeatResponse;
import com.mogujie.jarvis.worker.util.FutureUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeoutException;

/**
 * @author muming
 */
public class LogStorageHeartbeatThread extends Thread {

    private ActorSelection logStorageActor;
    private static final Logger LOGGER = LogManager.getLogger();

    public LogStorageHeartbeatThread(ActorSelection logStorageActor) {
        this.logStorageActor = logStorageActor;
    }

    @Override
    public void run() {
        Configuration workerConfig = ConfigUtils.getWorkerConfig();
        int workerGroupId = workerConfig.getInt(WorkerConfigKeys.WORKER_GROUP_ID, 0);
        String akkaPath = workerConfig.getString("logstorage.akka.path");
        String address = akkaPath.substring(akkaPath.indexOf("@") + 1);
        WorkerHeartBeatRequest request = WorkerHeartBeatRequest.newBuilder().setWorkerGroupId(workerGroupId).build();
        try {
            LogStorageHeartBeatResponse response = (LogStorageHeartBeatResponse) FutureUtils.awaitResult(logStorageActor, request, 30);
            if (!response.getSuccess()) {
                LOGGER.error("connect to logStorage(" + address + ") faulted ......" + response.getMessage());
            }
        } catch (TimeoutException e) {
            LOGGER.error("connect to logStorage(" + address + ") timeout......");
        } catch (Exception e) {
            LOGGER.error("connect to logStorage(" + address + ") exception ......", e);
        }
    }
}
