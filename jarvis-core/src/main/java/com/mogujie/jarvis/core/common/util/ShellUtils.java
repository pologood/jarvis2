/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2015年6月9日 下午1:18:35
 */
package com.mogujie.jarvis.core.common.util;

import java.io.IOException;

/**
 * @author wuya
 *
 */
public class ShellUtils {

    public static ProcessBuilder createProcessBuilder(String cmd) {
        return new ProcessBuilder("/bin/sh", "-c", cmd);
    }

    public static boolean executeShell(String cmd) {
        ProcessBuilder processBuilder = ShellUtils.createProcessBuilder(cmd);
        try {
            Process process = processBuilder.start();
            return process.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}
