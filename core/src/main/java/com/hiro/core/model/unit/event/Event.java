package com.hiro.core.model.unit.event;

import com.hiro.core.model.subject.Subject;

/**
 * Event of particular trigger
 * @param <T> who trigger this event
 * @param <V> Event result
 */
public interface Event<T extends Subject, V> {

    /**
     * Effect of this event
     * @param trigger who trigger this event
     * @return trigger
     */
    T trigger(T trigger);

    /**
     * Get event result
     * @return Event result
     */
    V getResult();

}
