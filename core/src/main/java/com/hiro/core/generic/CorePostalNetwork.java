package com.hiro.core.generic;

import com.hiro.core.model.assemblies.postal.PostalCenter;
import com.hiro.core.model.assemblies.postal.PostalNetwork;
import jakarta.annotation.PostConstruct;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@ToString
@Component
@Qualifier("core")
public class CorePostalNetwork extends PostalNetwork {

    public CorePostalNetwork(@Autowired @Qualifier("core") PostalCenter postalCenter) {
        this.acceptPostalCenters.add(postalCenter);
    }

    @PostConstruct
    public void init() {
        log.info("CorePostalNetwork initialized.");
    }

}
