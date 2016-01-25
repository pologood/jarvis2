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
 */
public class TestLogStorage {

    @Test
    public void testLog() throws ParseException, IOException {
//        testLogWrite();
        testLogRead();
    }

    private void testLogWrite() throws ParseException, IOException {

        LocalLogStream localLogStream = new LocalLogStream("1001_1002_1", StreamType.STD_OUT);
        String log;
        for (Integer i = 1; i < 100; i++) {
            log = "hello_测试大法好,棒棒哒!" + i.toString()+"\n";
            localLogStream.writeText(log);
        }
        localLogStream.writeEndFlag();

    }

    private void testLogRead() throws ParseException, IOException {
        LocalLogStream localLogStream = new LocalLogStream("1001_1002_1", StreamType.STD_OUT);
        long offset = 0;
        int i = 0;
        while (true) {
            i++;
            LogReadResult result = localLogStream.readText(offset, 5);
            String log = result.getLog();
            if (log.length() > 0) {
                System.out.printf(log + "\n");
            }

            offset = result.getOffset();
            if (result.isEnd()) {
                System.out.printf("\nend\n");
                break;
            }
            if (i > 3) {
                System.out.printf("\nout of test lines\n");
                break;
            }
        }
    }

}
