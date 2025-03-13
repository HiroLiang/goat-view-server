package com.hiro.core.model.parts.postal;

/**
 * Postal Code Issuer:
 * To Record and generate postal code
 */
public interface PostalSystem {

    /**
     * generate a postal code for user
     * @param name postal code name
     * @return PostalCode
     */
    PostalCode applyPostalCode(String name);

    /**
     * Check is this Postal code been registered
     * @param postalCode PostalCode
     * @return boolean
     */
    boolean isRegistered(PostalCode postalCode);

}
