package com.hiro.core.model.unit.store;

/**
 * Store: Store objects
 */
public interface Store {

    <T> void store(String key, T obj);

    <T> T retrieve(String key);

    Store inherit(Store store);

}
