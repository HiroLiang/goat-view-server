package com.hiro.core.test.assemblies.subject;

import com.hiro.core.test.components.event.Trigger;
import com.hiro.core.model.assemblies.store.GenericStore;
import com.hiro.core.model.components.store.Store;

/**
 * Subject of GenericEvent.
 * 1. Default to use GenericStore, super constructor with param in it.
 * 2. Define event trigger methods
 */
public abstract class Subject implements Store, Trigger {

    private final Store store;

    protected Subject() {
        this.store = new GenericStore();
    }

    protected Subject(Store store) {
        this.store = store;
    }

    @Override
    public <T> void store(String key, T obj) {
        store.store(key, obj);
    }

    @Override
    public <T> T retrieve(String key) {
        return store.retrieve(key);
    }

    @Override
    public Store inherit(Store store) {
        return store.inherit(store);
    }

}
