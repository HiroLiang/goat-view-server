package com.hiro.core.model.unit.event;

import java.util.List;

/**
 * Events Trigger
 */
public interface Trigger {

    /**
     * Reload single event
     * @param event Event
     * @return this
     */
    Trigger reload(Event<?, ?> event);

    /**
     * Reload List of events
     * @param events event list
     * @return this
     */
    Trigger reload(List<Event<?, ?>> events);

    /**
     * Trigger events loaded in this trigger
     */
    void trigger();

}
