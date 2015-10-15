package com.mogujie.jarvis.logstorage.logStream;

import com.mogujie.jarvis.core.JarvisConstants;
import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.logstorage.LogConstants;
import com.mogujie.jarvis.logstorage.domain.LogReadResult;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;


/**
 *
 */
public class LocalLogStream implements LogStream {

    private static final String LOG_PATH_LOCAL = ConfigUtils.getLogstorageConfig().
            getString("log.path.local", "/tmp/logs");

    private String logFile;


    public LocalLogStream(String fullId, StreamType streamType) {

        //日志文件
        String type = (streamType == StreamType.STD_OUT) ?  ".out" : ".err";
        this.logFile  = LOG_PATH_LOCAL + "/" + fullId + type;

    }


    /**
     * 写入日志
     *
     * @param log
     *
     * @throws java.io.IOException
     */
    @Override
    public void writeLine(String log) throws IOException {

        if (log == null || log.length() == 0) {
            return;
        }

        //写文件
        FileUtils.writeStringToFile(new File(logFile),
                log + JarvisConstants.LINE_SEPARATOR,
                StandardCharsets.UTF_8,
                true);

    }

    /**
     * 写入END标记
     *
     * @throws java.io.IOException
     */
    @Override
    public void writeEndFlag() throws IOException {

        writeLine(LogConstants.END_OF_LOG);

    }


    /**
     * 读取日志
     *
     * @param offset        ：偏移量
     * @param lines         ：读取行数
     * @return              ：读取内容返回
     * @throws java.io.IOException
     */
    public LogReadResult readLines(long offset, int lines) throws IOException {

        if (offset < 0) {
            offset = 0;
        }

        if (lines <= 0 || lines >= LogConstants.READ_MAX_LINES) {
            lines = LogConstants.READ_MAX_LINES;
        }

        try (RandomAccessFile raf = new RandomAccessFile(logFile, "r")) {


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
                if (line.contains(LogConstants.END_OF_LOG)) {
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



}
