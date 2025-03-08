package com.hiro.core.test.components.factory;

public interface Factory<T, K> {

    T getInstance(K enumeration);

}
