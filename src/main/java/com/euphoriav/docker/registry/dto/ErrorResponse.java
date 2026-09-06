package com.euphoriav.docker.registry.dto;

import com.euphoriav.docker.registry.enums.ErrorCode;

import java.util.List;

public record ErrorResponse(List<ErrorDto> errors) {

    public record ErrorDto(ErrorCode code, String message, String detail) {
    }
}
