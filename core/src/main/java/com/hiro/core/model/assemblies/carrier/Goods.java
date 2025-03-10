package com.hiro.core.model.assemblies.carrier;

import lombok.ToString;

/**
 * storage object
 * @param <T> class of instance
 */
@ToString
public class Goods<T> {

    private final T instance;

    public Goods(T instance) {
        this.instance = instance;
    }

    public T get() {
        return instance;
    }

}
