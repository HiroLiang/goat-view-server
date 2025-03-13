package com.hiro.core.model.assemblies.event;

import org.springframework.transaction.annotation.Transactional;

/**
 * Event Service:
 * 1. Simply wrap transactional for event.
 */
public class EventService {

    /**
     * Give event process transaction
     * @param event Event
     */
    @Transactional
    public void process(Event event) {
        event.process();
    }

    /**
     * Give event rollback transaction
     * @param event Event
     */
    @Transactional
    public void rollback(Event event) {
        event.rollback();
    }
}
