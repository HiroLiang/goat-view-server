package com.hiro.core.model.enumeration;

import lombok.Getter;

@Getter
public enum ErrorCode {

    /**
     * About transaction
     */
    ROLLBACK_SIGNAL("TR0001"),

    /**
     * About delivery
     */
    DELIVERY_MISSING_CARGO("DL0001"),
    DELIVERY_WITHOUT_PERMISSION("DL0002"),
    DELIVERY_ERROR("DL9999"),

    /**
     * About store
     */
    STORE_READ_WRITE_ERROR("ST00001"),

    /**
     * Unknown error
     */
    UNKNOWN_ERROR("SY9999")
    ;

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public static ErrorCode formCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.code.equals(code))
                return errorCode;
        }
        return UNKNOWN_ERROR;
    }

}
