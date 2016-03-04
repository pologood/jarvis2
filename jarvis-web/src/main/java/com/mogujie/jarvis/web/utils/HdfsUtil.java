package com.mogujie.jarvis.web.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import com.mogujie.bigdata.base.IOUtils;
import com.mogujie.bigdata.base.PropUtils;

/**
 * Created by muming on 16/2/23.
 */
public class HdfsUtil {

    public static String uploadFile2Hdfs(MultipartFile file, String title, String userName,boolean isDebugLocal) throws IOException {
        FileSystem fs=null;
        try {

            File tmpDirectory = null;
            File localFile = null;

            //拷贝到本地临时目录
            String fileName = title + ".jar";
            String localTmpDirectory = getLocalJarDir();
            tmpDirectory = new File(localTmpDirectory);
            if (!tmpDirectory.exists()) {
                tmpDirectory.mkdirs();
            }
            localFile = new File(tmpDirectory, fileName);
            localFile.delete();
            file.transferTo(localFile);
            if(isDebugLocal){
                return localTmpDirectory + fileName;
            }

            //上传到HDFS
            fs = initHadoopFileSystem();
            String distString = getHdfsJarDir(userName);
            Path distPath = new Path(distString);
            if (!fs.exists(distPath)) {
                fs.mkdirs(distPath);
            }
            fs.copyFromLocalFile(new Path(localFile.getAbsolutePath()), distPath);

            // 4. 删除本地临时文件
            localFile.delete();
            return distPath + fileName;
        } finally {
            IOUtils.closeQuietly(fs);
        }
    }

    /**
     * HDFS文件改名
     */
    public static String renameFile4Hdfs(String curUrl, String newTitle,boolean isDebugLocal) throws IOException {
        FileSystem fs=null;
        try {
            String dirPrefix = curUrl.substring(0,curUrl.lastIndexOf('/'));
            String newUrl = dirPrefix + newTitle + ".jar";

            if(isDebugLocal){
                return newUrl;
            }

            //上传到HDFS
            fs = initHadoopFileSystem();
            fs.rename(new Path(curUrl),new Path(newUrl));
            return newUrl;
        } finally {
            IOUtils.closeQuietly(fs);
        }
    }

    private static FileSystem initHadoopFileSystem() throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs;
        //设置Hadoop用户
        System.setProperty("HADOOP_USER_NAME", PropUtils.getProp("config.properties", "hdfs.super.account", false));
        conf.addResource(HdfsUtil.class.getResourceAsStream("/hdfs-site.xml"));
        fs = FileSystem.get(conf);
        return fs;
    }

    private static String getHdfsJarDir(String userName) {
        String uploadDir = PropUtils.getProp("config.properties", "upload.hdfs.dir", false);
        return (uploadDir.endsWith("/") ? uploadDir : uploadDir + "/") + userName + "/";
    }

    private static String getLocalJarDir() {
        return PropUtils.getProp("config.properties", "local.temp.jar.dir", "/tmp/jar", false);
    }

}

