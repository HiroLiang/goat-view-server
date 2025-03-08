package com.hiro.core.model.components.chain;

import java.util.List;
import java.util.function.Function;

/**
 * Define ChainList methods to chain chainable class.
 */
public interface ChainList {

    /**
     * Chain class directly
     * @param obj chainable class
     * @return this
     */
    ChainList chain(Chainable<?> obj);

    /**
     * Chain class if pass the check function
     * @param obj chainable class
     * @param function check function
     * @return this
     */
    ChainList chainIf(Chainable<?> obj, Function<Chainable<?>, Boolean> function);

    /**
     * Get chained list
     * @return List of chainable
     */
    List<Chainable<?>> get();

}
