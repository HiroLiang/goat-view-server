package com.hiro.core.model.unit.chain;

import java.util.function.Supplier;

/**
 * Chainable class. It would be constructed as a list by ChainList
 * @param <T> restrict the chain if needed
 */
public interface Chainable<T> {

    /**
     * Chain class with ChainList
     * @param obj chainable
     * @return ChainList
     */
    ChainList chain(Chainable<T> obj);

    /**
     * Chain class with ChainList if pass the check function
     * @param obj chainable
     * @param supplier check function
     * @return ChainList
     */
    ChainList chainIf(Chainable<T> obj, Supplier<Boolean> supplier);

}
