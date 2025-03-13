package com.hiro.core.model.parts.postal;

import com.hiro.core.model.assemblies.postal.PostalCenter;

import java.util.Objects;

/**
 * Identity for the postal system
 * @param name postal name. Define by user
 * @param code To verify the postal code
 * @param issuer Postal System
 */
public record PostalCode(String name, String code, PostalCenter issuer) {

    /**
     * Limited postal code not to be null
     * @param name name
     * @param code generate by postal system
     * @param issuer Postal System
     */
    public PostalCode {
        Objects.requireNonNull(name, "PostalCode name may not be null");
        Objects.requireNonNull(code, "PostalCode code may not be null");
    }

    /**
     * Override equals method
     * @param obj the reference object with which to compare.
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PostalCode other)
            return Objects.equals(name, other.name) && Objects.equals(code, other.code);
        return false;
    }
}
