package com.hiro.core.model.assemblies.event;

import com.hiro.core.model.assemblies.chain.GenericChainList;
import com.hiro.core.model.assemblies.subject.Subject;
import com.hiro.core.model.components.chain.ChainList;
import com.hiro.core.model.components.chain.Chainable;

import java.util.function.Supplier;

public abstract class Event implements Chainable<Subject> {

    @Override
    public ChainList chain(Chainable<Subject> obj) {
        return new GenericChainList(this, obj);
    }

    @Override
    public ChainList chainIf(Chainable<Subject> obj, Supplier<Boolean> supplier) {
        if (supplier.get()) return new GenericChainList(this, obj);
        return new GenericChainList(this);
    }
}
