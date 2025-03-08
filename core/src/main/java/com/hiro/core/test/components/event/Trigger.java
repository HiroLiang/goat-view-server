package com.hiro.core.test.components.event;

/**
 * Events Trigger
 */
public interface Trigger {

    /**
     * Trigger events loaded in this trigger
     */
    void pull(Event<?, ?> event);

}
