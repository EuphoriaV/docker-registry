package com.euphoriav.docker.registry.exception;

import com.euphoriav.docker.registry.enums.ErrorCode;

public class NotFoundException extends ApiException {

    public NotFoundException(String message, ErrorCode code) {
        super(message, code);
    }
}
