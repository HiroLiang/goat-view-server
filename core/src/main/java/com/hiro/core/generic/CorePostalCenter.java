package com.hiro.core.generic;

import com.hiro.core.model.assemblies.postal.PostalCenter;
import jakarta.annotation.PostConstruct;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Define a Postal Center for who didn't inherit PostalCenter
 */
@Slf4j
@ToString
public class CorePostalCenter extends PostalCenter {

    /**
     * Inject by config
     * @param secretKey ${postal.postal-center.secret-key}
     */
    public CorePostalCenter(String secretKey) {
        super(secretKey);
    }

    @PostConstruct
    public void postConstruct() {
        log.info("Use Default PostalCenter: {}... initialized!", this.getClass().getSimpleName());
    }

}
