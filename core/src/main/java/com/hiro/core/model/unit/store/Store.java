package com.hiro.core.model.unit.store;

/**
 * Store: Store objects
 */
public interface Store {

    /**
     * store object as Goods
     * @param key String
     * @param obj to be stored
     * @param <T> class of object
     */
    <T> void store(String key, T obj);

    /**
     * get stored object from store
     * @param key string
     * @return object
     * @param <T> class of object
     */
    <T> T retrieve(String key);

    /**
     * if a store need to inherit goods from other store
     * @param store the other store
     * @return this
     */
    Store inherit(Store store);

}
