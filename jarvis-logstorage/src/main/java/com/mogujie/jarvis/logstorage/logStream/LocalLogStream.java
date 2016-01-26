package com.mogujie.jarvis.logstorage.logStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UTFDataFormatException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.google.common.annotations.VisibleForTesting;
import com.mogujie.jarvis.core.util.IdUtils;
import com.mogujie.jarvis.logstorage.LogSetting;

import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.logstorage.LogConstants;
import com.mogujie.jarvis.logstorage.domain.LogReadResult;

/**
 * @author muming
 */
public class LocalLogStream implements LogStream {

    private String logFile;

    public LocalLogStream(String fullId, StreamType streamType) {
        String type = (streamType == StreamType.STD_OUT) ? ".out" : ".err";
        String logId = IdUtils.getLogIdFromFullId(fullId);
        logFile = LogSetting.LOG_LOCAL_PATH + "/" + logId + type;
    }

    /**
     * 写入日志
     *
     * @param text
     * @throws java.io.IOException
     */
    @Override
    public void writeText(String text) throws IOException {
        if (text == null || text.length() == 0) {
            return;
        }
        //写文件
        FileUtils.writeStringToFile(new File(logFile), text, StandardCharsets.UTF_8, true);
    }

    /**
     * 写入END标记
     *
     * @throws java.io.IOException
     */
    @Override
    public void writeEndFlag() throws IOException {
        writeText(String.valueOf(LogConstants.END_OF_LOG));
    }

    /**
     * 读取日志
     *
     * @param offset ：偏移量
     * @param size   ：字节数
     * @return ：读取内容返回
     * @throws java.io.IOException
     */
    @Override
    public LogReadResult readText(long offset, int size) throws IOException {
        if (offset < 0) {
            offset = 0;
        }

        if(size <= 0){
            return new LogReadResult(false, "", offset);
        }
        if (size > LogSetting.LOG_READ_MAX_SIZE) {
            size = LogSetting.LOG_READ_MAX_SIZE;
        }

        try (RandomAccessFile raf = new RandomAccessFile(logFile, "r")) {
            if (offset > raf.length()) {
                return new LogReadResult(false, "", raf.length());
            }
            raf.seek(offset);
            StringBuilder sb = new StringBuilder();
            int c;
            int i = 0;
            boolean isEnd = false;
            while (true) {
                c = readUtfChar(raf);
                if (c == -1) {
                    break;
                }
                //是否log结束
                if (c == LogConstants.END_OF_LOG) {
                    isEnd = true;
                    break;
                }
                i++;
                if (i > size) {
                    break;
                }
                offset = raf.getFilePointer();
                sb.append((char) c);
            }
            return new LogReadResult(isEnd, sb.toString(), offset);
        } catch (FileNotFoundException ex) {
            return new LogReadResult(true, "", 0);
        }
    }

    /**
     * 读取UTF字符(单个)
     * 代码参考java源码 java.io DataInputStream类 readUTF方法
     */
    private int readUtfChar(RandomAccessFile raf) throws IOException {
        int c1, c2, c3;
        int ret;
        c1 = raf.read();
        if (c1 == -1) return -1;
        switch (c1 >> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                /* 0xxxxxxx*/
                ret = c1;
                break;
            case 12:
            case 13:
                /* 110x xxxx   10xx xxxx*/
                c2 = (int) raf.readByte();
                if (c2 == -1) return -1;
                if ((c2 & 0xC0) != 0x80)
                    throw new UTFDataFormatException("uft转换出错. c1:" + c1 + ";c2" + c2);
                ret = (((c1 & 0x1F) << 6) | (c2 & 0x3F));
                break;
            case 14:
                /* 1110 xxxx  10xx xxxx  10xx xxxx */
                c2 = (int) raf.readByte();
                if (c2 == -1) return -1;
                c3 = (int) raf.readByte();
                if (c3 == -1) return -1;
                if (((c2 & 0xC0) != 0x80) || ((c3 & 0xC0) != 0x80))
                    throw new UTFDataFormatException("uft转换出错. c1:" + c1 + ";c2:" + c2 + ";c3:" + c3);
                ret = (((c1 & 0x0F) << 12) | ((c2 & 0x3F) << 6) | ((c3 & 0x3F) << 0));
                break;

            default:
                /* 10xx xxxx,  1111 xxxx */
                throw new UTFDataFormatException("uft转换出错. c1:" + c1);
        }
        return ret;

    }

    @VisibleForTesting
    public void clearLog() throws IOException {
        try {
            FileUtils.write(new File(logFile),"");
        } catch (IOException ex) {

        }
    }


//    /**
//     * 读取日志
//     *
//     * @param offset ：偏移量
//     * @param lines   ：行数
//     * @return ：读取内容返回
//     * @throws java.io.IOException
//     */
//    public LogReadResult readLine(long offset, int lines) throws IOException {
//        if (offset < 0) {
//            offset = 0;
//        }
//        if (lines <= 0 || lines >= LogConstants.READ_MAX_LINES) {
//            lines = LogConstants.READ_MAX_LINES;
//        }
//
//        try (RandomAccessFile raf = new RandomAccessFile(logFile, "r")) {
//            if (offset > raf.length()) {
//                return new LogReadResult(false, "", raf.length());
//            }
//            int readLines = 0;
//            raf.seek(offset);
//            StringBuilder sb = new StringBuilder();
//            String line;
//            boolean isEnd = false;
//            while ((line = raf.readLine()) != null) {
//                //是否log结束
//                if (line.contains(String.valueOf(LogConstants.END_OF_LOG))) {
//                    isEnd = true;
//                    break;
//                }
//                //是否超过读取行数
//                readLines++;
//                if (readLines > lines) {
//                    break;
//                }
//                offset = raf.getFilePointer();
//                sb.append(new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
//            }
//            return new LogReadResult(isEnd, sb.toString(), offset);
//        } catch (FileNotFoundException ex) {
//            return new LogReadResult(true, "", 0);
//        }
//    }


}



