package de.bitconex.adlatus.gate.config;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebServerConfig {
    private final HttpHandler httphandler;

    private WebServer http;
    @Value("${server.internalPort:80}")
    private int internalPort;

    public WebServerConfig(HttpHandler httphandler) {
        this.httphandler = httphandler;
    }

    @PostConstruct
    public void start() {
        ReactiveWebServerFactory factory = new NettyReactiveWebServerFactory(internalPort);
        this.http = factory.getWebServer(this.httphandler);
        this.http.start();
    }

    @PreDestroy
    public void stop() {
        this.http.stop();
    }
}
