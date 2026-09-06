package com.euphoriav.docker.registry.exception;

import com.euphoriav.docker.registry.dto.ErrorResponse;
import com.euphoriav.docker.registry.enums.ErrorCode;
import lombok.Getter;

import java.util.List;

public class ApiException extends RuntimeException {

    @Getter
    private final ErrorResponse errorResponse;

    public ApiException(String message, ErrorCode errorCode) {
        this(message, errorCode, "");
    }

    public ApiException(String message, ErrorCode errorCode, String detail) {
        super(message);
        this.errorResponse = new ErrorResponse(List.of(new ErrorResponse.ErrorDto(errorCode, message, detail)));
    }
}
