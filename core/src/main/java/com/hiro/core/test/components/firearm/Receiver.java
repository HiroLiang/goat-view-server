package com.hiro.core.test.components.firearm;

/**
 * An event magazine container
 */
public interface Receiver<T> {

    /**
     * Load magazine
     * @param magazine to load
     */
    void load(Magazine<T> magazine);

    /**
     * Eject magazine which is using
     */
    void eject();

    /**
     * Change magazine, get the rest one if it's not empty
     * @param magazine new Magazine
     * @return null or rest one
     */
    Magazine<T> change(Magazine<T> magazine);

}
