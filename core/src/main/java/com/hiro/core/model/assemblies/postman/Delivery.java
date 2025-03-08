package com.hiro.core.model.assemblies.postman;

import com.hiro.core.exceptioin.GenericException;
import com.hiro.core.model.enumeration.CargoType;
import com.hiro.core.model.enumeration.ErrorCode;

import java.time.LocalDateTime;
import java.util.Arrays;

public abstract class Delivery {

    private final Postbox postbox;

    private Cargo<?> cargo;

    protected Delivery(Postbox postbox) {
        this.postbox = postbox;
    }

    public <T> Delivery createCargo(CargoType type, T cargo, String... receiverCodes) {
        this.cargo = new Cargo<>(type, Arrays.stream(receiverCodes).toList(), cargo, LocalDateTime.now());

        return this;
    }

    public void deliver(PostNetwork postNetwork) {
        if (this.cargo == null) throw  new GenericException(ErrorCode.DELIVERY_MISSING_CARGO);

        postNetwork.submitCargo(this.postbox.getIdentity(), this.cargo);
    }

    public <T> Cargo<T> receive() {
        return this.postbox.poll();
    }

    public void subscribe(PostNetwork postNetwork) {
        postNetwork.subscribe(this.postbox);
    }

    public void unsubscribe(PostNetwork postNetwork) {
        postNetwork.cancel(this.postbox);
    }
}
