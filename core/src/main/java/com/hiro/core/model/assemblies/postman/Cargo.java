package com.hiro.core.model.assemblies.postman;

import com.hiro.core.model.enumeration.CargoType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Cargo<T> {

    public final CargoType type;

    public final Set<String> receiverCodes = ConcurrentHashMap.newKeySet();

    public final T cargo;

    public final LocalDateTime deliveryTime;

    protected Cargo(CargoType type, Collection<String> receiverCodes, T cargo, LocalDateTime deliveryTime) {
        this.type = type;
        this.receiverCodes.addAll(receiverCodes);
        this.cargo = cargo;
        this.deliveryTime = deliveryTime;
    }

    private Cargo(CargoType type, T cargo, LocalDateTime deliveryTime) {
        this.type = type;
        this.cargo = cargo;
        this.deliveryTime = deliveryTime;
    }

    public Cargo<T> getCopy() {
        return new Cargo<>(type, cargo, deliveryTime);
    }

}
