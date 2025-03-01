package com.hiro.core.model.unit.chain;

import java.util.function.Function;

public interface Chainable<T> {

    ChainList<T> chain(Chainable<T> obj);

    ChainList<T> chainIf(Chainable<T> obj, Function<?, Boolean> function);

}
