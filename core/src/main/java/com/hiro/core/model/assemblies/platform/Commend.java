package com.hiro.core.model.assemblies.platform;

import java.util.function.Consumer;

public class Commend {

    private final Consumer<Platform> consumer;

    public Commend(Consumer<Platform> consumer) {
        this.consumer = consumer;
    }

}
