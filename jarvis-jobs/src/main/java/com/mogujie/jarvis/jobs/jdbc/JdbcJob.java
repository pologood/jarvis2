/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年8月31日 下午9:55:32
 */
package com.mogujie.jarvis.jobs.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.mogujie.jarvis.core.JobContext;
import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.mogujie.jarvis.core.common.util.HiveQLUtil;
import com.mogujie.jarvis.core.exeception.JobException;
import com.mogujie.jarvis.core.job.AbstractJob;

/**
 * @author guangming
 *
 */
public abstract class JdbcJob extends AbstractJob {
    protected static final String COLUMNS_SEPARATOR = "\001";
    protected static int DEFAULT_MAX_QUERY_ROWS = 10000;
    private Connection connection;
    private Statement statement;

    /**
     * @param jobContext
     */
    public JdbcJob(JobContext jobContext) {
        super(jobContext);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean execute() throws JobException {
        Configuration config = ConfigUtils.getClientConfig();
        // LogCollector collector = getJobContext().getLogCollector();

        try {
            Class.forName(getDriverName());
            String user = getJobContext().getUser();
            String passwd = user;

            connection = DriverManager.getConnection(getJdbcUrl(config), user, passwd);
            Statement statement = connection.createStatement();
            final long startTime = System.currentTimeMillis();
            // collector.collectStderr("Querying " + getJobType() + "...");

            String hql = getJobContext().getCommand().trim();
            String[] cmds = HiveQLUtil.splitHiveScript(hql);
            for (String sql : cmds) {
                boolean hasResults = statement.execute(sql.trim());
                // some sql has no results,
                // e.g. "use db;", "set hive.cli.print.header=true;"...
                if (hasResults) {
                    ResultSet rs = statement.getResultSet();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    List<String> columns = new ArrayList<String>(columnCount);
                    for (int i = 1; i <= columnCount; i++) {
                        columns.add(metaData.getColumnName(i));
                    }

                    // collector.collectStdout(Joiner.on(COLUMNS_SEPARATOR).join(columns));
                    for (int i = 0; i < getMaxQueryRows(config) && rs.next(); i++) {
                        columns.clear();
                        for (int j = 1; j <= columnCount; j++) {
                            columns.add(rs.getString(j));
                        }
                        // collector.collectStdout(Joiner.on(COLUMNS_SEPARATOR)
                        // .useForNull("NULL").join(columns));
                    }
                }
            }

            final long endTime = System.currentTimeMillis();
            // collector.collectStderr("Finished, time taken: " + (endTime - startTime)
            // / 1000F + " seconds");

            return true;
        } catch (Exception e) {
            // collector.collectStderr(Throwables.getStackTraceAsString(e));
            return false;
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                    statement = null;
                }
                if (connection != null) {
                    connection.close();
                    connection = null;
                }
            } catch (SQLException e) {
                // Do nothing
            }
        }
    }

    @Override
    public boolean kill() throws JobException {
        // TODO Auto-generated method stub
        return false;
    }

    protected abstract String getJobType();

    protected abstract String getDriverName();

    protected abstract String getJdbcUrl(Configuration conf);

    protected abstract int getMaxQueryRows(Configuration conf);

}
