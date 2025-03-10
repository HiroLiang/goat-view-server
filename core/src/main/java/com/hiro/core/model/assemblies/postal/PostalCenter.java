package com.hiro.core.model.assemblies.postal;

import com.hiro.core.exceptioin.GenericException;
import com.hiro.core.model.enumeration.ErrorCode;
import com.hiro.core.model.parts.postal.PostalCode;
import com.hiro.core.model.parts.postal.PostalSystem;
import com.hiro.util.methods.EncryptUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class PostalCenter implements PostalSystem {

    private final String secretKey;

    private final Set<PostalCode> applicants = ConcurrentHashMap.newKeySet();

    protected PostalCenter(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public PostalCode applyPostalCode(String name) {
        if (hasName(name)) throw new GenericException(ErrorCode.POSTAL_DOUBLE_REGISTERED);

        PostalCode postalCode = new PostalCode(name, EncryptUtil.genHmac(name, secretKey), this);
        applicants.add(postalCode);

        return postalCode;
    }

    @Override
    public boolean isRegistered(PostalCode postalCode) {
        log.info("PostalCode: {}", postalCode);
        return applicants.contains(postalCode);
    }

    private boolean hasName(String name) {
        for (PostalCode postalCode : applicants) {
            if (postalCode.name().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }
}
