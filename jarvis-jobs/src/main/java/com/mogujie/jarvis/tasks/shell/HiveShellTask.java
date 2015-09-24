/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月10日 上午9:21:13
 */

package com.mogujie.jarvis.tasks.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.mogujie.jarvis.core.Task;
import com.mogujie.jarvis.core.TaskContext;
import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.mogujie.jarvis.core.exeception.ShellException;
import com.mogujie.jarvis.tasks.domain.HiveTaskEntity;
import com.mogujie.jarvis.tasks.util.HiveConfigUtils;
import com.mogujie.jarvis.tasks.util.MoguAnnotationUtils;
import com.mogujie.jarvis.tasks.util.MoguDateParamUtils;
import com.mogujie.jarvis.tasks.util.YarnUtils;

/**
 * @author wuya
 */
public class HiveShellTask extends ShellTask {

    private Set<String> applicationIdSet = new HashSet<>();
    private static final Pattern APPLICATION_ID_PATTERN = Pattern.compile("application_\\d+_\\d+");
    private static final Pattern MAPPERS_NUMBER_PATTERN = Pattern.compile("number of mappers: (\\d+);");

    private static final Logger LOGGER = LogManager.getLogger();

    public HiveShellTask(TaskContext jobContext, Set<String> applicationIdSet) {
        super(jobContext);
        this.applicationIdSet = applicationIdSet;
    }

    @Override
    public String getCommand() {
        Task task = getTaskContext().getTask();
        String user = null;
        HiveTaskEntity entity = HiveConfigUtils.getHiveJobEntry(task.getAppName());
        if (entity == null || (entity.isAdmin() && !task.getUser().trim().isEmpty())) {
            user = task.getUser();
        } else {
            user = entity.getUser();
        }

        Configuration workerConfig = ConfigUtils.getWorkerConfig();
        boolean isHive2Enable = workerConfig.getBoolean("hive2.enable", false);

        StringBuilder sb = new StringBuilder();
        sb.append("export HADOOP_USER_NAME=" + PinyinHelper.convertToPinyinString(user.trim(), "", PinyinFormat.WITHOUT_TONE) + ";");
        if (isHive2Enable) {
            String hive2Host = workerConfig.getString("hive2.host");
            sb.append("beeline --outputformat=tsv2 -u jdbc:hive2://" + hive2Host + " -n " + user);
        } else {
            sb.append("hive");
        }

        sb.append(" -e \"set hive.cli.print.header=true;");
        sb.append("set mapred.job.name=" + task.getTaskName() + ";");
        // 打印列名的时候不打印表名，否则xray无法显示数据
        sb.append("set hive.resultset.use.unique.column.names=false;");
        sb.append(MoguAnnotationUtils.removeAnnotation(MoguDateParamUtils.parse(task.getCommand())));
        sb.append("\"");
        return sb.toString();

    }

    @Override
    public void processStdOutputStream(InputStream inputStream) {
        Task task = getTaskContext().getTask();
        int currentResultRows = 0;
        int maxResultRows = 10000;
        String appName = task.getAppName();
        HiveTaskEntity entry = HiveConfigUtils.getHiveJobEntry(appName);
        if (entry != null) {
            maxResultRows = entry.getMaxResultRows();
        }

        String line = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            while ((line = br.readLine()) != null) {
                currentResultRows++;
                if (maxResultRows >= 0 && currentResultRows > maxResultRows) {
                    break;
                }
                getTaskContext().getLogCollector().collectStdout(line);
            }
            getTaskContext().getLogCollector().collectStdout("");
        } catch (IOException e) {
            LOGGER.error("error process stdouput stream", e);
        }
    }

    @Override
    public void processStdErrorStream(InputStream inputStream) {
        Task task = getTaskContext().getTask();
        int maxMapperNum = 2000;
        String appName = task.getAppName();
        HiveTaskEntity entry = HiveConfigUtils.getHiveJobEntry(appName);
        if (entry != null) {
            maxMapperNum = entry.getMaxMapperNum();
        }

        String line = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            while ((line = br.readLine()) != null) {
                getTaskContext().getLogCollector().collectStderr(line);

                // 从输出日志中提取application id
                String applicationId = match(APPLICATION_ID_PATTERN, 0, line);
                if (applicationId != null) {
                    applicationIdSet.add(applicationId);
                }

                // 检查map数是否超过限制，超过则kill掉任务
                String mappersNum = match(MAPPERS_NUMBER_PATTERN, 1, line);
                if (mappersNum != null) {
                    int num = Integer.parseInt(mappersNum);
                    if (maxMapperNum >= 0 && num > maxMapperNum) {
                        kill();
                        getTaskContext().getLogCollector().collectStderr("Job已被Kill，Map数量(" + num + ")超过(" + maxMapperNum + ")限制");
                        break;
                    }
                }
            }
            getTaskContext().getLogCollector().collectStderr("");
        } catch (IOException e) {
            LOGGER.error("error process stderr stream", e);
        }
    }

    @Override
    public boolean kill() {

        boolean result = true;

        result &= super.kill();

        // kill掉yarn application
        try {
            YarnUtils.killApplicationByIds(applicationIdSet);
        } catch (ShellException e) {
            result = false;
        }

        return result;
    }

    private String match(Pattern pattern, int group, String line) {
        Matcher m = pattern.matcher(line);
        if (m.find()) {
            return m.group(group);
        }

        return null;
    }

}