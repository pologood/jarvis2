package com.mogujie.jarvis.rest;

import com.mogujie.jarvis.core.util.ConfigUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.URI;

/**
 * Created by muming on 15/12/10.
 */
public class RestServerFactory {
    public static HttpServer createHttpServer() throws IOException {
        int port = ConfigUtils.getRestConfig().getInt("rest.http.port", 8080);
        ResourceConfig resourceConfig = new RestResourceConfig();
        URI baseUri = UriBuilder.fromUri("http://" + Inet4Address.getLocalHost().getHostAddress() + "/").port(port).build();
        return GrizzlyHttpServerFactory.createHttpServer(baseUri, resourceConfig);
    }
}
