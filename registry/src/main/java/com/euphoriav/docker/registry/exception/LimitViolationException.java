package com.euphoriav.docker.registry.exception;

import com.euphoriav.docker.registry.enums.ErrorCode;

public class LimitViolationException extends ApiException {

    public LimitViolationException(String message) {
        super(message, ErrorCode.MANIFEST_INVALID);
    }
}
