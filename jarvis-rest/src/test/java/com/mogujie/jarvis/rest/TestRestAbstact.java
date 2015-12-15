package com.mogujie.jarvis.rest;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.Inet4Address;

/**
 * Created by muming on 15/12/10.
 */
public class TestRestAbstact {

    private static HttpServer server;

    protected static String baseUrl;

    public static void before() throws IOException{
        baseUrl = "http://" + Inet4Address.getLocalHost().getHostAddress();
        server = RestServerFactory.createHttpServer();
        server.start();
    }

    public static void after(){
        server.shutdown();
    }

}
