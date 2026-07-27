package com.euphoriav.docker.registry.exception;

import com.euphoriav.docker.registry.dto.ErrorResponse;

public class InvalidRequestException extends ApiException {
    public InvalidRequestException(String message, ErrorResponse.ErrorCode code) {
        super(message, code);
    }
}
