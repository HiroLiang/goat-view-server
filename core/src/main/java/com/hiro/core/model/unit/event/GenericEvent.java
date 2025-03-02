package com.hiro.core.model.unit.event;

import com.hiro.core.model.subject.Subject;
import com.hiro.core.model.unit.chain.ChainList;
import com.hiro.core.model.unit.chain.Chainable;
import com.hiro.core.model.unit.chain.GenericChainList;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Define what generic event work, and implement chainable
 * @param <T> subject to trigger the event
 * @param <V> result of event
 */
public abstract class GenericEvent<T extends Subject, V> implements Event<T, V>, Chainable<T> {

    private Function<Event<?, ?>[], Boolean> loadCheck;

    private Event<?, ?>[] loadConditions;

    /**
     * Chain class with ChainList
     * @param obj chainable
     * @return ChainList
     */
    @Override
    public ChainList chain(Chainable<T> obj) {
        return new GenericChainList(obj);
    }

    /**
     * Chain class with ChainList if pass the check function
     * @param obj chainable
     * @param supplier check function
     * @return ChainList
     */
    @Override
    public ChainList chainIf(Chainable<T> obj, Supplier<Boolean> supplier) {
        if (supplier.get()) return new GenericChainList(this, obj);
        return new GenericChainList(this);
    }

    /**
     * If without trigger (If there is an event without trigger, define a client as trigger)
     * @param trigger Who trigger this event
     * @return trigger
     */
    @Override
    public T trigger(T trigger) {
        if (trigger == null ||
                (loadCheck != null && !loadCheck.apply(loadConditions)))
            return trigger;
        return this.doEvent(trigger);
    }

    /**
     * What would happen while trigger, override if extend this class.
     * @param trigger Who trigger this event
     * @return trigger
     */
    protected abstract T doEvent(T trigger);

    /**
     * Add a load check, event will be trigger if pass the check
     * @param function load check
     * @param events conditions
     * @return this
     */
    public GenericEvent<T, V> loadIf(Function<Event<?, ?>[], Boolean> function, Event<?, ?>... events) {
        this.loadCheck = function;
        this.loadConditions = events;
        return this;
    }

}
