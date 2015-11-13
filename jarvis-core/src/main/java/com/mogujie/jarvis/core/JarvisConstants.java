package com.mogujie.jarvis.core;

/**
 * @author wuya
 *
 */
public interface JarvisConstants {

    public static final String WORKER_AKKA_SYSTEM_NAME = "worker";
    public static final String SERVER_AKKA_SYSTEM_NAME = "server";
    public static final String LOGSTORAGE_AKKA_SYSTEM_NAME = "logstorage";
    public static final String REST_AKKA_SYSTEM_NAME = "rest";

    public static final String WORKER_AKKA_USER_PATH = "/user/" + WORKER_AKKA_SYSTEM_NAME;
    public static final String SERVER_AKKA_USER_PATH = "/user/" + SERVER_AKKA_SYSTEM_NAME;
    public static final String LOGSTORAGE_AKKA_USER_PATH = "/user/" + LOGSTORAGE_AKKA_SYSTEM_NAME;
    public static final String REST_AKKA_USER_PATH = "/user/" + REST_AKKA_SYSTEM_NAME;

    public static final String EMPTY_STRING = "";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
}
