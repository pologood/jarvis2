package com.mogujie.jarvis.core;

import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * @author wuya
 */
public interface JarvisConstants {

    public static final String SERVER_AKKA_SYSTEM_NAME = "server";
    public static final String SERVER_AKKA_USER_NAME = "server";
    public static final String SERVER_AKKA_USER_PATH = "/user/" + SERVER_AKKA_USER_NAME;

    public static final String WORKER_AKKA_SYSTEM_NAME = "worker";
    public static final String WORKER_AKKA_USER_NAME = "worker";
    public static final String WORKER_AKKA_USER_PATH = "/user/" + WORKER_AKKA_USER_NAME;

    public static final String REST_AKKA_SYSTEM_NAME = "rest";
    public static final String REST_AKKA_USER_NAME = "rest";
    public static final String REST_AKKA_USER_PATH = "/user/" + REST_AKKA_USER_NAME;

    public static final String LOGSTORAGE_AKKA_SYSTEM_NAME = "logstorage";
    public static final String LOGSTORAGE_AKKA_USER_NAME = "logstorage";
    public static final String LOGSTORAGE_AKKA_USER_PATH = "/user/" + LOGSTORAGE_AKKA_USER_NAME;

    public static final String EMPTY_STRING = "";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String HTTP_CALLBACK_URL = "httpCallbackUrl";

    public static final DateTime DATETIME_MAX = new DateTime(9999, 12, 31, 0, 0, 0, DateTimeZone.forOffsetHours(0));
    public static final DateTime DATETIME_MIN = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeZone.forOffsetHours(0));

    public static final int BIZ_GROUP_ID_UNKNOWN = 0;

    public static final Pattern IP_PATTERN = Pattern
            .compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
}
