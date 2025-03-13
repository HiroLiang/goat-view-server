package com.hiro.core.model.assemblies.platform;

public abstract class Platform {

    private final Processor processor;

    private final Dock dock;

    protected Platform(Dock dock, Processor processor) {
        this.dock = dock;
        this.processor = processor;
    }
}
