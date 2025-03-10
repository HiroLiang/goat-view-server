package com.hiro.core.model.parts.registration;

public interface Registrable<T> {

    void subscribe(Applicant<T> applicant);

    void unsubscribe(Applicant<T> applicant);

}
