package com.hiro.core.model.enumeration;

import lombok.Getter;

@Getter
public enum ErrorCode {

    /**
     * About transaction
     */
    ROLLBACK_SIGNAL("TR0001"),

    /**
     * About postal
     */
    POSTAL_MISSING_PARCEL("PO0001"),
    POSTAL_MISSING_IDENTITY("PO0002"),
    POSTAL_WITHOUT_PERMISSION("PO0003"),
    POSTAL_DOUBLE_REGISTERED("PO0004"),
    POSTAL_UNKNOWN_POSTCODE("PO0005"),
    POSTAL_DESTROYED("DL0003"),
    POSTAL_ERROR("PO9999"),

    /**
     * About dock
     */
    DOCK_ERROR("DL9999"),

    /**
     * About storage
     */
    STORAGE_READ_WRITE_ERROR("ST0001"),

    /**
     * About encrypt
     */
    ENCRYPTION_ERROR("EC0001"),

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
