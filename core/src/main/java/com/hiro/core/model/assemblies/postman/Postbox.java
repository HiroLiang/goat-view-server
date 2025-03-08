package com.hiro.core.model.assemblies.postman;

import com.hiro.core.model.components.postman.Subscriber;
import com.hiro.core.test.assemblies.event.QueueMagazine;
import com.hiro.core.test.components.firearm.Magazine;

public class Postbox implements Subscriber {

    private final String postalCode;

    private final Magazine<Cargo<?>> magazine =new QueueMagazine<>();

    public Postbox(String postalCode) {
        this.postalCode = postalCode;
    }

    public void put(Cargo<?> cargo) {
        if (cargo != null && cargo.receiverCodes.contains(postalCode)) {
            magazine.loadRound(cargo);
            cargo.receiverCodes.remove(postalCode);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Cargo<T> poll() {
        Cargo<?> cargo = magazine.feed();
        if (cargo != null) {
            return (Cargo<T>) cargo;
        }
        return null;
    }

    @Override
    public String getIdentity() {
        return this.postalCode;
    }
}
