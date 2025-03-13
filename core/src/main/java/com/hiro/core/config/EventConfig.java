package com.hiro.core.config;

import com.hiro.core.model.assemblies.event.EventService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfig {

    @Bean
    @ConditionalOnMissingBean
    public EventService eventService() {
        return new EventService();
    }

}
