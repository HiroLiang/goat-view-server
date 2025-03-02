package com.hiro.core.model.subject;

import com.hiro.core.model.unit.event.Event;
import com.hiro.core.model.unit.event.Trigger;
import com.hiro.core.model.unit.store.GenericStore;
import com.hiro.core.model.unit.store.Store;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Subject implements Store, Trigger {

    private final Store store = new GenericStore();

    protected final Set<Event<?, ?>> events = new HashSet<>();

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

    @Override
    @SuppressWarnings("unchecked")
    public void trigger() {
        for (Event<?, ?> event : events) {
            ((Event<Subject, ?>) event).trigger(this);
        }
    }

    @Override
    public Trigger reload(List<Event<?, ?>> events) {
        this.events.clear();
        this.events.addAll(events);
        return this;
    }

    @Override
    public Trigger reload(Event<?, ?> event) {
        this.events.clear();
        this.events.add(event);
        return this;
    }
}
