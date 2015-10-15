package com.mogujie.jarvis.logstorage.logStream;

import com.mogujie.jarvis.logstorage.domain.LogReadResult;

import java.io.IOException;

/**
 * Created by muming on 15/10/15.
 */
public interface LogStream {

    //写日志——一行
    public void writeLine(String log) throws IOException;

    //写日志——结束标志
    public void writeEndFlag() throws IOException;

    /**
     * 读取日志
     *
     * @param offset        ：偏移量
     * @param lines         ：读取行数
     * @return              ：读取内容返回
     * @throws java.io.IOException
     */
    public LogReadResult readLines(long offset, int lines) throws IOException;

}
