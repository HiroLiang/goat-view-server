package com.hiro.core.model.unit.event;

public interface Event<T> {

    Event<T> process(T inject);

}
