package com.hiro.core.exceptioin;

import com.hiro.core.model.enumeration.ErrorCode;

public class GenericException extends RuntimeException {

    public GenericException(ErrorCode code) {
        super(code.getCode());
    }

    public GenericException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getCode(), cause);
    }

}
