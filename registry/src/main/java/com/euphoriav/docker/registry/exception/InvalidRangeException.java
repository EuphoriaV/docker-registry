package com.euphoriav.docker.registry.exception;

import com.euphoriav.docker.registry.enums.ErrorCode;

public class InvalidRangeException extends ApiException {

    public InvalidRangeException(String message) {
        super(message, ErrorCode.BLOB_UPLOAD_INVALID);
    }
}
