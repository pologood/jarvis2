/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月14日 上午10:32:59
 */

package com.mogujie.jarvis.rest;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;

/**
 * 启动RestServer
 *
 */
public class JarvisRest {

    static{
        //FOR GrizzlyHttpServer`logger change
        System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager");
    }
    private  static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        LOGGER.info("Starting rest server...");
        HttpServer server = RestServerFactory.createHttpServer();
        server.start();
        LOGGER.info("Rest server started.");
    }

}