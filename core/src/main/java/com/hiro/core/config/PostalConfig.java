package com.hiro.core.config;

import com.hiro.core.generic.CorePostalCenter;
import com.hiro.core.generic.CorePostalNetwork;
import com.hiro.core.model.assemblies.postal.PostalCenter;
import com.hiro.core.model.assemblies.postal.PostalNetwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Define default postal component
 */
@Configuration
public class PostalConfig {

    @Bean
    @Qualifier("core")
    @ConditionalOnMissingBean
    public PostalCenter postalCenter(@Value("${postal.postal-center.secret-key:postal-key}") String secretKey) {
        return new CorePostalCenter(secretKey);
    }

    @Bean
    @Qualifier("core")
    @ConditionalOnMissingBean
    public PostalNetwork postalNetwork(@Autowired @Qualifier("core") PostalCenter postalCenter) {
        return new CorePostalNetwork(postalCenter);
    }

}
