package com.hiro.core.model.assemblies.store;

import com.hiro.core.exceptioin.GenericException;
import com.hiro.core.model.enumeration.ErrorCode;
import com.hiro.core.model.assemblies.carrier.Goods;
import com.hiro.core.model.parts.store.Storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GenericStorage implements Storage {

    private final Map<String, Goods<?>> storage = new ConcurrentHashMap<>();

    /**
     * store object as Goods
     * @param key String
     * @param obj to be stored
     * @param <T> class of object
     */
    @Override
    public <T> void store(String key, T obj) {
        if (key == null || obj == null) throw new GenericException(ErrorCode.STORAGE_READ_WRITE_ERROR);
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
     * get stored object and pull this object from this shelves
     * @param key string
     * @return string
     * @param <T> class of object
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T remove(String key) {
        Goods<?> goods = storage.remove(key);
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
    public Storage inherit(Storage store) {
        if (store instanceof GenericStorage otherStore) {
            for (Map.Entry<String, Goods<?>> entry : otherStore.storage.entrySet()) {
                this.storage.putIfAbsent(entry.getKey(), new Goods<>(entry.getValue().get()));
            }
        }
        return this;
    }
}
