package com.hiro.core.model.parts.postal;

import com.hiro.core.model.assemblies.postal.PostalCenter;

import java.util.Objects;

public record PostalCode(String name, String code, PostalCenter issuer) {
    public PostalCode {
        Objects.requireNonNull(name, "PostalCode name may not be null");
        Objects.requireNonNull(code, "PostalCode code may not be null");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PostalCode other)
            return Objects.equals(name, other.name) && Objects.equals(code, other.code);
        return false;
    }
}
