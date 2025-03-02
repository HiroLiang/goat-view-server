package com.hiro.demo.environment.events;

import com.hiro.core.model.unit.event.GenericEvent;
import com.hiro.demo.environment.FireHill;

public class Burned extends GenericEvent<FireHill, Void> {

    @Override
    protected FireHill doEvent(FireHill fireHill) {
        fireHill.getCasters().forEach(caster -> caster.setLife(caster.getLife() - 10));
        return fireHill;
    }

    @Override
    public Void getResult() {
        return null;
    }
}
