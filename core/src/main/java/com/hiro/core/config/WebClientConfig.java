package com.hiro.core.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    @ConditionalOnMissingBean(WebClient.class)
        public WebClient webClient(@Autowired ConnectionProvider connectionProvider) {
        log.info("Initialing WebClient version: [Core] ...");
        HttpClient client = HttpClient.create(connectionProvider)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS))
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(client))
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(ConnectionProvider.class)
    public ConnectionProvider connectionProvider() {
        log.info("Initialing ConnectionProvider version: [Core] ...");
        return ConnectionProvider.builder("core-pool")
                .maxConnections(300) // max connection count
                .pendingAcquireMaxCount(100) // max pending count
                .pendingAcquireTimeout(Duration.ofSeconds(10)) // pending time out
                .maxIdleTime(Duration.ofSeconds(30)) // unused time out
                .maxLifeTime(Duration.ofMinutes(2)) // total lifetime
                .evictInBackground(Duration.ofSeconds(10)) // duration / GC
                .lifo() // lifo: new first, fifo: old first
                .build();
    }

}
