/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月17日 上午10:30:12
 */
package com.mogujie.jarvis.core.common.util;

import com.google.common.base.Throwables;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * @author wuya
 *
 */
public class ConfigUtils {

    private static PropertiesConfiguration clientConfig;
    private static PropertiesConfiguration serverConfig;

    public synchronized static Configuration getClientConfig() {
        if (clientConfig == null) {
            try {
                clientConfig = new PropertiesConfiguration("client.properties");
                clientConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
            } catch (ConfigurationException e) {
                Throwables.propagate(e);
            }
        }

        return clientConfig;
    }

    public synchronized static Configuration getServerConfig() {
        if (serverConfig == null) {
            try {
                serverConfig = new PropertiesConfiguration("server.properties");
                serverConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
            } catch (ConfigurationException e) {
                Throwables.propagate(e);
            }
        }

        return serverConfig;
    }

    public static Config getAkkaConfig() {
        try {
            String ipv4 = Inet4Address.getLocalHost().getHostAddress();
            return ConfigFactory.parseString("akka.remote.netty.tcp.hostname=" + ipv4);
        } catch (UnknownHostException e) {
            Throwables.propagate(e);
        }

        return null;
    }

}
