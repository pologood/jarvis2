/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年12月15日 下午7:54:15
 */

package com.mogujie.jarvis.server.guice4test;

import com.google.inject.Provider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Map.Entry;
import java.util.Properties;

public class DataSourceProvider4Test implements Provider<DataSource> {

    private HikariDataSource dataSource;

    public DataSourceProvider4Test(Properties properties) {
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(properties.getProperty("dataSourceClassName"));
        for (Entry<Object, Object> entry : properties.entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith("dataSource.")) {
                config.addDataSourceProperty(key.replace("dataSource.", ""), entry.getValue());
            }
        }
        dataSource = new HikariDataSource(config);
    }

    @Override
    public DataSource get() {
        return dataSource;
    }

}
