/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月21日 下午4:11:14
 */

package com.mogujie.jarvis.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import akka.actor.ActorSystem;
import akka.routing.SmallestMailboxPool;

import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.server.actor.ServerActor;
import com.mogujie.jarvis.server.domain.JarvisTimerTask;
import com.mogujie.jarvis.server.util.SpringContext;
import com.mogujie.jarvis.server.util.SpringExtension;

/**
 *
 *
 */
public class JarvisServer {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) throws Exception {
        LOGGER.info("Starting Jarvis server...");

        ApplicationContext context = SpringContext.getApplicationContext();
        ActorSystem system = JarvisServerActorSystem.getInstance();
        SpringExtension.SPRING_EXT_PROVIDER.get(system).initialize(context);

        system.actorOf(new SmallestMailboxPool(10).props(ServerActor.props()), JarvisConstants.SERVER_AKKA_SYSTEM_NAME);

        init();

        LOGGER.info("Jarvis server started.");
    }

    public static void init() throws ParseException {
        //24 hours
        final long time24h = 24 * 60 * 60 * 1000;
        final String startTime = "00:00:00";
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd " + startTime);
        Date firstTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sdf.format(new Date()));
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new JarvisTimerTask(), firstTime, time24h);
    }

}
