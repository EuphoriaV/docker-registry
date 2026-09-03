package com.euphoriav.docker.registry.exception;

import com.euphoriav.docker.registry.dto.ErrorResponse;

public class LimitViolationException extends ApiException {

    public LimitViolationException(String message) {
        super(message, ErrorResponse.ErrorCode.MANIFEST_INVALID);
    }
}
