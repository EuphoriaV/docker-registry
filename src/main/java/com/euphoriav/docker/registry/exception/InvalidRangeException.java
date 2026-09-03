package com.euphoriav.docker.registry.exception;

import com.euphoriav.docker.registry.dto.ErrorResponse;

public class InvalidRangeException extends ApiException {

    public InvalidRangeException(String message) {
        super(message, ErrorResponse.ErrorCode.BLOB_UPLOAD_INVALID);
    }
}
