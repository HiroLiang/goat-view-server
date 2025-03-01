package com.hiro.core.model.unit.chain;

import java.util.List;
import java.util.function.Function;

public interface ChainList<T> {

    ChainList<T> chain(Chainable<T> obj);

    ChainList<T> chainIf(Chainable<T> obj, Function<?, Boolean> function);

    List<T> get();

}
