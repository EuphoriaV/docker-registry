package com.euphoriav.docker.registry.enums;

public enum ErrorCode {
    BLOB_UNKNOWN,
    BLOB_UPLOAD_UNKNOWN,
    BLOB_UPLOAD_INVALID,
    SIZE_INVALID,
    DIGEST_INVALID,
    NAME_INVALID,
    MANIFEST_INVALID,
    MANIFEST_BLOB_UNKNOWN,
    MANIFEST_UNKNOWN;
}