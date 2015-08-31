/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午10:07:19
 */

package com.mogujie.jarvis.jobs.jdbc;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.JobContext;

/**
 * @author guangming
 *
 */
public class MysqlJdbcJob extends JdbcJob {

    /**
     * @param jobContext
     */
    public MysqlJdbcJob(JobContext jobContext) {
        super(jobContext);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected String getJobType() {
      return "mysql";
    }

    @Override
    protected String getDriverName() {
      return "com.mysql.jdbc.Driver";
    }

    @Override
    protected String getJdbcUrl(Configuration conf) {
      return conf.getString("mysql.jdbc.url");
    }

    @Override
    protected int getMaxQueryRows(Configuration conf) {
      return conf.getInt("mysql.max.query.rows", DEFAULT_MAX_QUERY_ROWS);
    }

}
