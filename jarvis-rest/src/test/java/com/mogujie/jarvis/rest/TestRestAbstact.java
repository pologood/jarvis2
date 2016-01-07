package com.mogujie.jarvis.rest;

import java.io.IOException;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.core.util.IPUtils;

/**
 * Created by muming on 15/12/10.
 */
public class TestRestAbstact {

    private static HttpServer server;

    protected static String baseUrl;

    @BeforeClass
    public static void before() throws IOException {
        int port = ConfigUtils.getRestConfig().getInt("rest.http.port", 8080);
        baseUrl = "http://" + IPUtils.getIPV4Address() + ":" + port;
        server = RestServerFactory.createHttpServer();
        server.start();
    }

    @AfterClass
    public static void after() {
        server.shutdown();
    }

}
