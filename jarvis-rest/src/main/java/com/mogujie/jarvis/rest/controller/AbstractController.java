package com.mogujie.jarvis.rest.controller;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.google.protobuf.GeneratedMessage;

import com.mogujie.jarvis.rest.MsgCode;
import com.mogujie.jarvis.rest.RestResult;
import com.mogujie.jarvis.rest.vo.AbstractVo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;


/**
 * 控制器父类
 */
public abstract class AbstractController {


//    private ActorSystem system;
//    private String serverAkkaPath;
//    private String workerAkkaPath;

    protected ActorSelection logCenterActor;
    protected ActorSelection serverActor;

    protected static final Timeout TIMEOUT = new Timeout(Duration.create(30, TimeUnit.SECONDS));
    protected static final Logger LOGGER = LogManager.getLogger();


    public AbstractController(ActorSystem system, String serverAkkaPath, String logCenterAkkaPath) {

//        this.system = system;
//        this.serverAkkaPath = serverAkkaPath;
//        this.workerAkkaPath = workerAkkaPath;

        serverActor = system.actorSelection(serverAkkaPath + "/user/server");
        logCenterActor = system.actorSelection(logCenterAkkaPath + "/user/logCenter");

    }


    protected GeneratedMessage callActor(ActorSelection actor,GeneratedMessage request) throws java.lang.Exception{

        return callActor(actor,request, TIMEOUT);

    }

    protected  GeneratedMessage callActor(ActorSelection actor,GeneratedMessage request,Timeout timeout) throws java.lang.Exception{

        Future<Object> future = Patterns.ask(actor, request, timeout);
        GeneratedMessage response = (GeneratedMessage) Await.result(future, timeout.duration());
        return response;

    }


    /**
     * 返回错误结果
     * @param msg
     * @return
     */
    protected RestResult errorResult(String msg) {
        return errorResult(MsgCode.UNKNOWN,msg);
    }

	/**
	 * 返回错误结果
	 * @param msg
	 * @return
	 */
	protected RestResult errorResult(int code, String msg) {
        RestResult result = new RestResult(false);
        result.setCode(code);
		result.setMsg(msg);
		return result;
	}

	/**
	 * 成功结果
	 * @return
	 */
	protected RestResult successResult() {
		return new RestResult(true);
	}

	/**
	 * 成功结果
	 * @param data
	 * @return
	 */
	protected RestResult successResult(AbstractVo data) {
        RestResult result = new RestResult(true);
		result.setData(data);
		return result;
	}



}
