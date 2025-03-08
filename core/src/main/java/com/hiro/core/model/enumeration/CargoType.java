package com.hiro.core.model.enumeration;

import com.hiro.core.model.assemblies.event.Event;
import com.hiro.core.model.assemblies.platform.Commend;
import com.hiro.core.model.components.store.Goods;
import lombok.Getter;

@Getter
public enum CargoType {

    EVENT(Event.class),
    COMMEND(Commend.class),
    GOODS(Goods.class),
    MESSAGE(String.class)
    ;

    private final Class<?> clazz;

    CargoType(Class<?> clazz) {
        this.clazz = clazz;
    }
}
