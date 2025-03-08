package com.hiro.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    private final Map<String, Sinks.One<String>> tasks = new ConcurrentHashMap<>();

    @GetMapping("/mono")
    public Mono<String> testMono() {
        String taskId = UUID.randomUUID().toString();
        tasks.put(taskId, Sinks.one());
        Sinks.One<String> sink = tasks.get(taskId);
        sink.tryEmitValue("hello");
        return sink.asMono();
    }

}
