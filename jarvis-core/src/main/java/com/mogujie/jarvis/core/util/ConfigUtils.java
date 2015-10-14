/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月17日 上午10:30:12
 */
package com.mogujie.jarvis.core.util;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import com.google.common.base.Throwables;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * @author wuya
 *
 */
public class ConfigUtils {

    private static PropertiesConfiguration workerConfig;
    private static PropertiesConfiguration serverConfig;
    private static PropertiesConfiguration logstorageConfig;

    /**
     * 读取Server配置
     *
     * @return
     */
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

    /**
     * 读取worker配置
     * 
     * @return
     */
    public synchronized static Configuration getWorkerConfig() {
        if (workerConfig == null) {
            try {
                workerConfig = new PropertiesConfiguration("worker.properties");
                workerConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
            } catch (ConfigurationException e) {
                Throwables.propagate(e);
            }
        }

        return workerConfig;
    }

    /**
     * 读取logstorage配置
     * 
     * @return
     */
    public synchronized static Configuration getLogstorageConfig() {
        if (logstorageConfig == null) {
            try {
                logstorageConfig = new PropertiesConfiguration("logstorage.properties");
                logstorageConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
            } catch (ConfigurationException e) {
                Throwables.propagate(e);
            }
        }

        return logstorageConfig;
    }

    /**
     * 读取rest配置
     * 
     * @return
     */
    public synchronized static Configuration getRestConfig() {
        if (logstorageConfig == null) {
            try {
                logstorageConfig = new PropertiesConfiguration("rest.properties");
                logstorageConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
            } catch (ConfigurationException e) {
                Throwables.propagate(e);
            }
        }

        return logstorageConfig;
    }

    /**
     * 获取Akka的配置，并追加通用默认配置
     *
     * @param fileName
     * @return
     */
    public static Config getAkkaConfigWithCommon(String fileName) {

        Config akkaConfig = ConfigUtils.getAkkaConfig(fileName);

        try {
            String ipv4 = Inet4Address.getLocalHost().getHostAddress();
            return ConfigFactory.parseString("akka.remote.netty.tcp.hostname=" + ipv4).withFallback(akkaConfig);
        } catch (UnknownHostException e) {
            Throwables.propagate(e);
        }

        return akkaConfig;
    }

    /**
     * 获取Akka的配置
     *
     * @param fileName
     * @return
     */
    public static Config getAkkaConfig(String fileName) {

        return  ConfigFactory.load(fileName);
    }


}
