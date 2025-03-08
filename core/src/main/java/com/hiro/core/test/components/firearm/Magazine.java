package com.hiro.core.test.components.firearm;

import java.util.List;

/**
 * Event container
 */
public interface Magazine<T> {

    /**
     * Load single event
     * @param round Round
     * @return this
     */
    Magazine<T> loadRound(T round);

    /**
     * Load List of events
     * @param rounds round list
     * @return this
     */
    Magazine<T> loadRounds(List<T> rounds);

    /**
     * Feed a round
     * @return Event ( Round )
     */
    T feed();

    /**
     * Check rounds
     * @return boolean
     */
    boolean isEmpty();

}
