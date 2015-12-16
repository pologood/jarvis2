package com.mogujie.jarvis.rest;

import com.mogujie.jarvis.core.util.ConfigUtils;
import com.mogujie.jarvis.core.util.IPUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

/**
 * RestServer工厂
 * @author muming
 */
public class RestServerFactory {
    public static HttpServer createHttpServer() throws IOException {
        int port = ConfigUtils.getRestConfig().getInt("rest.http.port", 8080);
        URI baseUri = UriBuilder.fromUri("http://" + IPUtils.getIPV4Address() + "/").port(port).build();
        ResourceConfig resourceConfig = new RestResourceConfig();
        return GrizzlyHttpServerFactory.createHttpServer(baseUri, resourceConfig);
    }
}
