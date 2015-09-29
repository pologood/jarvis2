/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月14日 上午10:32:59
 */

package com.mogujie.jarvis.rest;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.mogujie.jarvis.rest.controller.JobController;
import com.mogujie.jarvis.rest.controller.SystemController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;

/**
 * 启动RestServer
 *
 */
public class RestServer {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        LOGGER.info("Starting rest server...");

        int port = ConfigUtils.getServerConfig().getInt("rest.http.port", 8080);
        Config config = ConfigFactory.load("akka-rest.conf");
        Config restfulConfig = ConfigUtils.getAkkaConfig().withFallback(config.getConfig("rest"));

        String serverAkkaPath = restfulConfig.getString("jarvis.server.akka.path");
        String logServerAkkaPath = restfulConfig.getString("jarvis.logserver.akka.path");

        ActorSystem system = ActorSystem.create(JarvisConstants.REST_SERVER_AKKA_SYSTEM_NAME, restfulConfig);
        URI baseUri = UriBuilder.fromUri("http://" + Inet4Address.getLocalHost().getHostAddress() + "/").port(port).build();
        ResourceConfig resourceConfig = new ResourceConfig();

        resourceConfig.register(new SystemController(system, serverAkkaPath, logServerAkkaPath));
        resourceConfig.register(new JobController(system, serverAkkaPath, logServerAkkaPath));


        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, resourceConfig);
        server.start();

        LOGGER.info("Rest server started.");
    }

}
