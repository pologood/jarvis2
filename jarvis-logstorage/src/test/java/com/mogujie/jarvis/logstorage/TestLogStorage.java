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

import org.junit.Test;

import com.mogujie.jarvis.core.domain.StreamType;
import com.mogujie.jarvis.logstorage.domain.LogReadResult;
import com.mogujie.jarvis.logstorage.util.LogUtil;

/**
 * @author 牧名
 *
 */
public class TestLogStorage {

    @Test
    public void testLogWrite() throws ParseException, IOException {

        String fileName = LogUtil.getLogPath4Local("fullid_test001", StreamType.STD_OUT);

        String log;
        for (Integer i = 1; i < 100; i++) {
            log = "hello " + i.toString();
            LogUtil.writeLine4Local(fileName, log);

        }

        LogUtil.writeEndFlag2Local(fileName);

    }

    @Test
    public void testLogRead() throws ParseException, IOException {

        String fileName = LogUtil.getLogPath4Local("fullid_test001", StreamType.STD_OUT);

        long offset = 0;
        int i = 0;
        while (true) {
            i++;

            LogReadResult result = LogUtil.readLines4locale(fileName, offset, 60);

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
