package com.mogujie.jarvis.logstorage.util;

import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.logstorage.domain.LogReadResult;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;


/**
 *
 */
public class LogUtil {


    private static final int READ_MAX_LINES = 10000;    //log读取最大件数

    private static final String END_OF_LOG = "\004";    //log结束符号 4  EOT (end of transmission) 传输结束

    private static final String LOG_PATH_LOCAL = ConfigUtils.getLogstorageConfig().getString("log.path.local");


    /**
     * 获取日志文件名_本地
     *
     * @param fullId
     * @param streamType
     * @return
     */
    public static String getLogPath4Local(String fullId, StreamType streamType) {

        if(fullId == null || fullId.length() ==0){
            return "";
        }

        String type = (streamType == StreamType.STD_OUT) ?  ".out" : ".err";

        return  LOG_PATH_LOCAL + "/" + fullId + type;

    }

    /**
     * 写入日志_本地文件
     *
     * @param filePathName
     * @param log
     * @throws java.io.IOException
     */
    public static void writeLine4Local(String filePathName, String log) throws java.io.IOException {

        if (log == null || log.length() == 0) {
            return;
        }

        if (filePathName == null || filePathName.length() == 0) {
            return;
        }

        //写文件
        FileUtils.writeStringToFile(new File(filePathName),
                log + JarvisConstants.LINE_SEPARATOR,
                StandardCharsets.UTF_8,
                true);

    }

    /**
     * 写入END标记_本地文件
     *
     * @param filePathName          :文件名
     * @throws java.io.IOException
     */
    public static void writeEndFlag2Local(String filePathName) throws java.io.IOException {

        writeLine4Local(filePathName, END_OF_LOG);

    }


    /**
     * 读取日志_本地
     *
     * @param filePathName  ：文件名称
     * @param offset        ：偏移量
     * @param lines         ：读取行数
     * @return              ：读取内容返回
     * @throws IOException
     */
    public static LogReadResult readLines4locale(String filePathName, long offset, int lines) throws IOException {

        if (filePathName == null || filePathName.length() == 0) {
            return new LogReadResult(true, "", 0);
        }

        if (offset < 0) {
            offset = 0;
        }

        if (lines <= 0 || lines >= READ_MAX_LINES) {
            lines = READ_MAX_LINES;
        }

        try (RandomAccessFile raf = new RandomAccessFile(filePathName, "r")) {


            if (offset > raf.length()) {
                return new LogReadResult(false, "", raf.length());
            }

            int readLines = 0;
            raf.seek(offset);

            StringBuilder sb = new StringBuilder();
            String line;
            boolean isEnd = false;
            while ((line = raf.readLine()) != null) {


                //是否log结束
                if (line.contains(END_OF_LOG)) {
                    isEnd = true;
                    break;
                }

                //是否超过读取行数
                readLines++;
                if (readLines > lines) {
                    break;
                }

                offset = raf.getFilePointer();
                sb.append(new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                sb.append(JarvisConstants.LINE_SEPARATOR);

            }
            return new LogReadResult(isEnd, sb.toString(), offset);

        }

    }


    //--------------------------------- HDFS  预留方法 ----------------------------

    /**
     * 写入日志_HDFS
     */
    public static void writeLine4Hdfs(String file, String log) {


    }




}
