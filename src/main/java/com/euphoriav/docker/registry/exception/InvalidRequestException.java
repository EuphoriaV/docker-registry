package com.euphoriav.docker.registry.exception;

import com.euphoriav.docker.registry.enums.ErrorCode;

public class InvalidRequestException extends ApiException {

    public InvalidRequestException(String message, ErrorCode code) {
        super(message, code);
    }

    public InvalidRequestException(String message, ErrorCode code, String detail) {
        super(message, code, detail);
    }
}
