package com.hiro.core.model.parts.factory;

public interface Factory<T, K> {

    T generate(K identity);

}
