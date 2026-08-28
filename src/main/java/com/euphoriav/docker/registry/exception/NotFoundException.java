package com.euphoriav.docker.registry.exception;

import com.euphoriav.docker.registry.dto.ErrorResponse;

public class NotFoundException extends ApiException {

    public NotFoundException(String message, ErrorResponse.ErrorCode code) {
        super(message, code);
    }
}
