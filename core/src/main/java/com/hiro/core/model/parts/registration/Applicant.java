package com.hiro.core.model.parts.registration;

/**
 * An applier with identity
 * @param <T> identity class
 */
public interface Applicant<T> {

    /**
     * Get defined identity
     * @return Identity
     */
    T getIdentity();

}
