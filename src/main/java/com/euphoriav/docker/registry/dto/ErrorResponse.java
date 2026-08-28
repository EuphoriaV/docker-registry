package com.euphoriav.docker.registry.dto;

import java.util.List;

public record ErrorResponse(List<ErrorDto> errors) {

    public record ErrorDto(ErrorCode code, String message, String detail) {
    }

    public enum ErrorCode {
        BLOB_UNKNOWN, BLOB_UPLOAD_UNKNOWN, BLOB_UPLOAD_INVALID, SIZE_INVALID, DIGEST_INVALID, NAME_INVALID, MANIFEST_INVALID, MANIFEST_BLOB_UNKNOWN;
    }
}
