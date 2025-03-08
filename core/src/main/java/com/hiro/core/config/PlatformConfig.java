package com.hiro.core.config;

import com.hiro.core.model.assemblies.platform.Platform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PlatformConfig {

    @Bean
    @ConditionalOnMissingBean
    public Platform platform() {
        return null;
    }

}
