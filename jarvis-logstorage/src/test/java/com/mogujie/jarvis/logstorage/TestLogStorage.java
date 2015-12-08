/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年1月14日 下午11:06:53
 */
package com.mogujie.jarvis.logstorage;

import java.io.IOException;
import java.text.ParseException;

import com.mogujie.jarvis.logstorage.logStream.LocalLogStream;
import org.junit.Test;

import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.logstorage.domain.LogReadResult;

/**
 * @author 牧名
 *
 */
public class TestLogStorage {

    @Test
    public void testLogWrite() throws ParseException, IOException {

        LocalLogStream localLogStream = new LocalLogStream("1001_1002_1",StreamType.STD_OUT);
        String log;
        for (Integer i = 1; i < 100; i++) {
            log = "hello " + i.toString();
            localLogStream.writeLine(log);
        }
        localLogStream.writeEndFlag();

    }

    @Test
    public void testLogRead() throws ParseException, IOException {
        LocalLogStream localLogStream = new LocalLogStream("1001_1002_1",StreamType.STD_OUT);
        long offset = 0;
        int i = 0;
        while (true) {
            i++;
            LogReadResult result = localLogStream.readLines(offset, 60);
            String log = result.getLog();
            if (log.length() > 0) {
                System.out.printf(log);
            }

            offset = result.getOffset();
            if (result.isEnd()) {
                System.out.printf("end");
                break;
            }
            if (i > 1000) {
                System.out.printf("out of test lines");
                break;
            }
        }
    }

}
