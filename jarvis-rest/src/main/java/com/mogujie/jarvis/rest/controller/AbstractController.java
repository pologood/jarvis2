package com.mogujie.jarvis.rest.controller;

import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.protobuf.GeneratedMessage;
import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.domain.AkkaType;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.rest.MsgCode;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.vo.AbstractVo;
import com.typesafe.config.Config;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * 控制器父类
 */
public abstract class AbstractController {

    private static ActorSystem system;

    private String serverAkkaUserPath;
    private String workerAkkaUserPath;
    private String logstorageAkkaUserPath;

    private ActorSelection workerActor;
    private ActorSelection serverActor;
    private ActorSelection logstorageActor;

    protected static final Timeout TIMEOUT = new Timeout(Duration.create(30, TimeUnit.SECONDS));
    protected static final Logger LOGGER = LogManager.getLogger();

    public AbstractController() {

        Configuration restConfig = ConfigUtils.getRestConfig();
        serverAkkaUserPath = restConfig.getString("server.akka.path") + JarvisConstants.SERVER_AKKA_USER_PATH;
        workerAkkaUserPath = restConfig.getString("worker.akka.path") + JarvisConstants.WORKER_AKKA_USER_PATH;
        logstorageAkkaUserPath = restConfig.getString("logstorage.akka.path") + JarvisConstants.LOGSTORAGE_AKKA_USER_PATH;

        if (system == null) {
            Config akkaConfig = ConfigUtils.getAkkaConfig("akka-rest.conf");
            system = ActorSystem.create(JarvisConstants.REST_AKKA_SYSTEM_NAME, akkaConfig);
        }

    }

    public ActorSystem getActorSystem() {
        return system;
    }

    /**
     * 调用Actor
     *
     * @param request
     * @param timeout
     * @return
     * @throws java.lang.Exception
     */
    protected GeneratedMessage callActor(AkkaType akkaType, GeneratedMessage request, Timeout timeout) throws java.lang.Exception {

        ActorSelection actor;

        if (akkaType == AkkaType.SERVER) {
            if (serverActor == null) {
                serverActor = system.actorSelection(serverAkkaUserPath);
            }
            actor = serverActor;

        } else if (akkaType == AkkaType.WORKER) {
            if (workerActor == null) {
                workerActor = system.actorSelection(workerAkkaUserPath);
            }
            actor = workerActor;

        } else if (akkaType == AkkaType.LOGSTORAGE) {
            if (logstorageActor == null) {
                logstorageActor = system.actorSelection(logstorageAkkaUserPath);
            }

            actor = logstorageActor;
        } else {
            return null;
        }

        Future<Object> future = Patterns.ask(actor, request, timeout);
        GeneratedMessage response = (GeneratedMessage) Await.result(future, timeout.duration());
        return response;

    }

    protected GeneratedMessage callActor(AkkaType akkaType, GeneratedMessage request) throws java.lang.Exception {

        return callActor(akkaType, request, TIMEOUT);

    }

    /**
     * 返回错误结果
     *
     * @param msg
     * @return
     */
    protected RestResult errorResult(String msg) {
        return errorResult(MsgCode.UNDEFINE_ERROR, msg);
    }

    /**
     * 返回错误结果
     *
     * @param msg
     * @return
     */
    protected RestResult errorResult(int code, String msg) {
        RestResult result = new RestResult();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    /**
     * 成功结果
     *
     * @return
     */
    protected RestResult successResult() {
        return new RestResult(MsgCode.SUCCESS);
    }

    /**
     * 成功结果
     *
     * @param data
     * @return
     */
    protected RestResult successResult(AbstractVo data) {
        RestResult result = new RestResult(MsgCode.SUCCESS);
        result.setData(data);
        return result;
    }

}
