package com.hiro.core.test.components.event;

/**
 * Event of particular trigger
 * @param <T> who trigger this event
 * @param <V> target
 */
public interface Event<T extends Trigger, V> {

    /**
     * trigger event
     * @param trigger who trigger this event
     * @param target event occur target
     */
    void occur(T trigger, V target);

    /**
     * Process while Distributed Transactions rollback
     * @param target event occur target
     */
    void rollback(V target);

}
