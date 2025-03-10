package com.hiro.core.model.parts.registration;

import com.hiro.core.model.parts.postal.PostalCode;

/**
 * Can be subscribed by Applicant
 * @param <T> identity of Applicant
 */
public interface Registrable<T> {

    /**
     * Record this Applicant
     * @param applicant applier
     */
    void subscribe(Applicant<T> applicant);

    /**
     * Remove this Applicant
     * @param applicant applier
     */
    void unsubscribe(Applicant<T> applicant);

    /**
     * Check is applicant subscribed
     * @param applicant Applicant
     * @return boolean
     */
    public boolean isSubscribed(Applicant<PostalCode> applicant);

}
