package com.hiro.core.model.components.store;

/**
 * storage object
 * @param <T> class of instance
 */
public class Goods<T> {

    private final T instance;

    public Goods(T instance) {
        this.instance = instance;
    }

    public T get() {
        return instance;
    }

}
