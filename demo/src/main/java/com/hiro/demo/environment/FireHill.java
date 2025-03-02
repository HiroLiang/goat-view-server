package com.hiro.demo.environment;

import com.hiro.core.model.environment.Environment;
import com.hiro.core.model.subject.Subject;
import com.hiro.demo.subject.Caster;

import java.util.HashSet;
import java.util.Set;

public class FireHill extends Environment {

    public Set<Caster> getCasters() {
        Set<Caster> casters = new HashSet<>();
        for (Subject subject : subjects.get(Caster.class)) {
            if (subject instanceof Caster caster) {
                casters.add(caster);
            }
        }
        return casters;
    }

}
