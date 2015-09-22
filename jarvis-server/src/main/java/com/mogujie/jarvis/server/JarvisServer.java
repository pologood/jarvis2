/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月21日 下午4:11:14
 */

package com.mogujie.jarvis.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.server.actor.ServerActor;
import com.mogujie.jarvis.server.util.SpringContext;
import com.mogujie.jarvis.server.util.SpringExtension;

import akka.actor.ActorSystem;

/**
 * 
 *
 */
public class JarvisServer {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        LOGGER.info("Starting Jarvis server...");

        ApplicationContext context = SpringContext.getApplicationContext();
        ActorSystem system = JarvisServerActorSystem.getInstance();
        SpringExtension.SPRING_EXT_PROVIDER.get(system).initialize(context);

        system.actorOf(ServerActor.props(), JarvisConstants.SERVER_AKKA_SYSTEM_NAME);

        LOGGER.info("Jarvis server started.");
    }

}
