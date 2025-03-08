package com.hiro.core.model.assemblies.chain;

import com.hiro.core.model.components.chain.ChainList;
import com.hiro.core.model.components.chain.Chainable;

import java.util.function.Supplier;

/**
 * Generic chainable class, use GenericChainList.
 * 1. If you just need a chainable class, extends it to use chain.
 * 2. If you want another ChainList or override chain method, extends and override methods.
 */
public abstract class GenericChainable implements Chainable<Void> {

    /**
     * Chain class with ChainList
     * @param obj chainable
     * @return ChainList
     */
    @Override
    public ChainList chain(Chainable<Void> obj) {
        return new GenericChainList(this, obj);
    }

    /**
     * Chain class with ChainList if pass the check function
     * @param obj chainable
     * @param supplier check function
     * @return ChainList
     */
    @Override
    public ChainList chainIf(Chainable<Void> obj, Supplier<Boolean> supplier) {
        if (supplier.get()) return new GenericChainList(this, obj);
        return new GenericChainList(this);
    }

}
