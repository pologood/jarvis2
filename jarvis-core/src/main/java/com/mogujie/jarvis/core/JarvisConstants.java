package com.mogujie.jarvis.core;

/**
 * @author wuya
 *
 */
public interface JarvisConstants {

    public static final String WORKER_AKKA_SYSTEM_NAME = "worker";
    public static final String SERVER_AKKA_SYSTEM_NAME = "server";
    public static final String LOGSERVER_AKKA_SYSTEM_NAME = "logserver";
    public static final String REST_SERVER_AKKA_SYSTEM_NAME = "rest";

    public static final String WORKER_AKKA_USER_PATH = "/user/worker";
    public static final String SERVER_AKKA_USER_PATH = "/user/server";
    public static final String LOGSERVER_AKKA_USER_PATH = "/user/logserver";
    public static final String REST_SERVER_AKKA_USER_PATH = "/user/rest";

    public static final String EMPTY_STRING = "";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
}
