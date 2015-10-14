/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月21日 下午3:55:58
 */

package com.mogujie.jarvis.server;

import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;

/**
 * 
 *
 */
public class JarvisServerActorSystem {

    private static Config config = ConfigFactory.load("akka-server.conf");
    private static ActorSystem system = ActorSystem.create(JarvisConstants.SERVER_AKKA_SYSTEM_NAME, ConfigUtils.getCommonAkkaConfig().withFallback(config));

    private JarvisServerActorSystem() {
    }

    public static ActorSystem getInstance() {
        return system;
    }

}
