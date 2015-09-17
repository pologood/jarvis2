package com.mogujie.jarvis.logstorage.util;

import com.mogujie.jarvis.core.common.util.ConfigUtils;
import com.mogujie.jarvis.core.domain.StreamType;

/**
 * Created by muming on 15/9/17.
 */
public class LogUtil {

    private static final String LOG_DIR = ConfigUtils.getLogstorageConfig().getString("logserver.log.dir");

    public static String getLogPath(long jobId, StreamType logType){

        String path = LOG_DIR + "/" + jobId;
        if (logType == StreamType.STD_OUT) {
            path += ".out";
        } else {
            path += ".err";
        }

        return path;

    }



}
