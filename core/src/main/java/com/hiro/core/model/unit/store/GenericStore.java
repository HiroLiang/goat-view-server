package com.hiro.core.model.unit.store;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic store:
 * defined methods for who need to use store
 */
public class GenericStore implements Store{

    private final Map<String, Goods<?>> storage = new HashMap<>();

    /**
     * store object as Goods
     * @param key String
     * @param obj to be stored
     * @param <T> class of object
     */
    @Override
    public <T> void store(String key, T obj) {
        this.storage.put(key, new Goods<>(obj));
    }

    /**
     * get stored object from store
     * @param key string
     * @return object
     * @param <T> class of object
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T retrieve(String key) {
        Goods<?> goods = storage.get(key);
        if (goods == null) {
            return null;
        }
        return (T) goods.get();
    }

    /**
     * if a store need to inherit goods from other store
     * @param store the other store
     * @return this
     */
    @Override
    public Store inherit(Store store) {
        if (store instanceof GenericStore otherStore) {
            for (Map.Entry<String, Goods<?>> entry : otherStore.storage.entrySet()) {
                this.storage.putIfAbsent(entry.getKey(), new Goods<>(entry.getValue().get()));
            }
        }
        return this;
    }

    /**
     * storage object
     * @param <T> class of instance
     */
    public static class Goods<T> {

        private final T instance;

        public Goods(T instance) {
            this.instance = instance;
        }

        public T get() {
            return instance;
        }
    }

}
