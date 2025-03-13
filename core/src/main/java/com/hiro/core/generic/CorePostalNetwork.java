package com.hiro.core.generic;

import com.hiro.core.model.assemblies.postal.PostalCenter;
import com.hiro.core.model.assemblies.postal.PostalNetwork;
import jakarta.annotation.PostConstruct;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ToString
public class CorePostalNetwork extends PostalNetwork {

    public CorePostalNetwork(PostalCenter... postalCenters) {
        this.acceptPostalCenters.addAll(List.of(postalCenters));
    }

    @PostConstruct
    public void postConstruct() {
        log.info("Use default postal network: {}... Initialized!", this.getClass().getSimpleName());
    }

}
