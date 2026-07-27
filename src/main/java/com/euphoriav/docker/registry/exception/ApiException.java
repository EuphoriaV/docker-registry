package com.euphoriav.docker.registry.exception;

import com.euphoriav.docker.registry.dto.ErrorResponse;
import lombok.Getter;

import java.util.List;

public class ApiException extends RuntimeException {

    @Getter
    private final ErrorResponse errorResponse;

    public ApiException(String message, ErrorResponse.ErrorCode errorCode) {
        super(message);
        this.errorResponse = new ErrorResponse(List.of(new ErrorResponse.ErrorDto(errorCode, message, "")));
    }
}
