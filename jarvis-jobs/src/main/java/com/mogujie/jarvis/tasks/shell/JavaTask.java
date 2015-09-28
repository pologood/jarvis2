/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年9月28日 下午4:43:25
 */

package com.mogujie.jarvis.tasks.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.mogujie.jarvis.core.TaskContext;
import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.mogujie.jarvis.core.domain.TaskDetail;
import com.mogujie.jarvis.core.exeception.TaskException;

/**
 * 
 *
 */
public class JavaTask extends ShellTask {

    private static final String HDFS_ROOT_PATH = ConfigUtils.getWorkerConfig().getString("hdfs.jar.root.path");
    private static final String LOCAL_ROOT_PATH = ConfigUtils.getWorkerConfig().getString("local.jar.root.path");

    public JavaTask(TaskContext taskContext) {
        super(taskContext);
    }

    private void downloadJarFromHDFS(String filename) throws IOException {
        TaskDetail taskDetail = getTaskContext().getTaskDetail();
        String hdfsFilePath = HDFS_ROOT_PATH + "/" + taskDetail.getUser() + "/" + filename;
        String localFilePath = LOCAL_ROOT_PATH + "/" + taskDetail.getUser() + "/" + filename;

        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        Path p1 = new Path(hdfsFilePath);
        Path p2 = new Path("file://" + localFilePath);

        if (!fs.exists(p1)) {
            throw new FileNotFoundException(hdfsFilePath);
        }

        long hdfsJarModificationTime = fs.getFileLinkStatus(p1).getModificationTime();
        File localFile = new File(localFilePath);
        File parentFile = new File(localFile.getParent());
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        if (!localFile.exists() || hdfsJarModificationTime > localFile.lastModified()) {
            fs.copyToLocalFile(p1, p2);
            localFile.setLastModified(hdfsJarModificationTime);
            fs.close();
        }
    }

    @Override
    public void preExecute() throws TaskException {
        String jobName = getTaskContext().getTaskDetail().getTaskName() + ".jar";
        String filename = jobName.replaceFirst("\\w+_\\d+_\\d+_", "");
        try {
            downloadJarFromHDFS(filename);
        } catch (IOException e) {
            throw new TaskException(e);
        }
    }

    protected String getCmd(String localFilePath) {
        return "java -cp " + localFilePath;
    }

    @Override
    public String getCommand() {
        TaskDetail taskDetail = getTaskContext().getTaskDetail();
        String jobName = taskDetail.getTaskName() + ".jar";
        String filename = jobName.replaceFirst("\\w+_\\d+_\\d+_", "");

        String localFilePath = LOCAL_ROOT_PATH + "/" + taskDetail.getUser() + "/" + filename;
        return getCmd(localFilePath) + " " + taskDetail.getContent();
    }

}
