package com.euphoriav.docker.registry.dto;

import java.util.List;

public record ErrorResponse(List<ErrorDto> errors) {
    public record ErrorDto(ErrorCode code, String message, String details) {
    }

    public enum ErrorCode {
        BLOB_UPLOAD_UNKNOWN, BLOB_UPLOAD_INVALID, SIZE_INVALID;
    }
}
