package com.hiro.core.model.assemblies.chain;

import com.hiro.core.model.components.chain.ChainList;
import com.hiro.core.model.components.chain.Chainable;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@NoArgsConstructor
public class GenericChainList implements ChainList {

    private final List<Chainable<?>> chains = new ArrayList<>();

    public GenericChainList(Chainable<?>... chains) {
        this.chains.addAll(Arrays.asList(chains));
    }

    /**
     * Chain class directly
     * @param obj chainable class
     * @return this
     */
    @Override
    public ChainList chain(Chainable<?> obj) {
        chains.add(obj);
        return this;
    }

    /**
     * Chain class if pass the check function
     * @param obj chainable class
     * @param function check function
     * @return this
     */
    @Override
    public ChainList chainIf(Chainable<?> obj, Function<Chainable<?>, Boolean> function) {
        if (function.apply(obj)) chains.add(obj);
        return this;
    }

    /**
     * Get chained list
     * @return List of chainable
     */
    @Override
    public List<Chainable<?>> get() {
        return this.chains;
    }
}
