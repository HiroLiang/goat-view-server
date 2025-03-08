package com.hiro.core.model.components.postman;

public interface Subscribable<T extends Subscriber> {

    void subscribe(T subscriber);

    void cancel(T subscriber);

}
