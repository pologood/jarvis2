/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月21日 下午3:55:58
 */

package com.mogujie.jarvis.server;

import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;

/**
 * 
 *
 */
public class JarvisServerActorSystem {

    private static Config config = ConfigFactory.load("akka-server.conf").getConfig("server");
    private static ActorSystem system = ActorSystem.create("server", ConfigUtils.getAkkaConfig().withFallback(config));

    private JarvisServerActorSystem() {
    }

    public static ActorSystem getInstance() {
        return system;
    }

}
