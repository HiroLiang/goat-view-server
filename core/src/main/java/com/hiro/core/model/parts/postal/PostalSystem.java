package com.hiro.core.model.parts.postal;

public interface PostalSystem {

    PostalCode applyPostalCode(String name);

    boolean isRegistered(PostalCode postalCode);

}
