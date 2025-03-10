package com.hiro.core.generic;

import com.hiro.core.model.assemblies.postal.PostalCenter;
import jakarta.annotation.PostConstruct;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@ToString
@Component
@Qualifier("core")
public class CorePostalCenter extends PostalCenter {

    public CorePostalCenter(@Value("${postal.postal-center.secret-key:12345}") String secretKey) {
        super(secretKey);
    }

    @PostConstruct
    public void init() {
        log.info("PostalCenter init success");
    }

}
