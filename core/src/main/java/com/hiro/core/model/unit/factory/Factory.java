package com.hiro.core.model.unit.factory;

public interface Factory<T, K> {

    T getInstance(K enumeration);

}
