package com.euphoriav.docker.registry.exception;

import com.euphoriav.docker.registry.dto.ErrorResponse;

public class InvalidRequestException extends ApiException {
    public InvalidRequestException(String message, ErrorResponse.ErrorCode code) {
        super(message, code);
    }
    public InvalidRequestException(String message, ErrorResponse.ErrorCode code, String detail) {
        super(message, code, detail);
    }
}
