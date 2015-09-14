/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月18日 下午3:19:28
 */
package com.mogujie.jarvis.rest;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import akka.actor.ActorSystem;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;

/**
 * @author wuya
 *
 */
@Path("server")
public class RestResource {

    private ActorSystem system;
    private String serverAkkaPath;
    private String logServerAkkaPath;

    private static final Timeout TIMEOUT = new Timeout(Duration.create(30, TimeUnit.SECONDS));
    private static final Logger LOGGER = LogManager.getLogger();

    public RestResource(ActorSystem system, String serverAkkaPath, String logServerAkkaPath) {
        this.system = system;
        this.serverAkkaPath = serverAkkaPath;
        this.logServerAkkaPath = logServerAkkaPath;
    }

}
