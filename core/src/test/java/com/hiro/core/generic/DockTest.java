package com.hiro.core.generic;

import com.hiro.core.CoreApplication;
import com.hiro.core.model.assemblies.platform.Dock;
import com.hiro.core.model.assemblies.postal.PostalCenter;
import com.hiro.core.model.assemblies.postal.PostalNetwork;
import com.hiro.core.model.assemblies.postal.Postbox;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = CoreApplication.class)
public class DockTest {

    @Autowired
    @Qualifier("core")
    private PostalNetwork postalNetwork;

    @Autowired
    @Qualifier("core")
    private PostalCenter postalCenter;

//    private static final CountDownLatch latch = new CountDownLatch(1);

    @Test
    void TestCoreDock() throws InterruptedException {
        log.info("Use postal network: {}", postalNetwork);
        log.info("Use postal center: {}", postalCenter);

        Postbox postbox1 = new Postbox(postalCenter.applyPostalCode("01"));
        Postbox postbox2 = new Postbox(postalCenter.applyPostalCode("02"));
        log.info("Postbox 1: {}", postbox1);
        log.info("Postbox 2: {}", postbox2);

        this.postalNetwork.subscribe(postbox1);
        this.postalNetwork.subscribe(postbox2);

        Dock dock1 = new CoreDock(postbox1, postalNetwork);
        Dock dock2 = new CoreDock(postbox2, postalNetwork);
        log.info("Dock 1: {}", dock1);
        log.info("Dock 2: {}", dock2);

        this.postalNetwork.start();
        dock1.start();
        dock2.start();

        for (int i = 0; i < 5 ; i ++) {
            dock1.ship(postbox2.getIdentity().code(), "hello", String.class);
            Thread.sleep(100);
        }

        Thread.sleep(3000);

        this.postalNetwork.pause();
        this.postalNetwork.start();
        dock2.pause();

        for (int i = 0; i < 5 ; i ++) {
            dock1.ship(postbox2.getIdentity().code(), "hello", String.class);
            Thread.sleep(100);
        }

        Thread.sleep(3000);

        dock2.start();
        this.postalNetwork.restart();

        for (int i = 0; i < 5 ; i ++) {
            dock1.ship(postbox2.getIdentity().code(), "hello", String.class);
            Thread.sleep(100);
        }

        Thread.sleep(3000);

        dock1.destroy();
        dock2.destroy();
        this.postalNetwork.destroy();

        log.info("Test CoreDock finished");
    }
}
