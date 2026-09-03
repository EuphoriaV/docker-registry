package com.euphoriav.docker.registry.controller;

import com.euphoriav.docker.registry.dto.ErrorResponse;
import com.euphoriav.docker.registry.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class RegistryControllerAdvice {

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidRequestException e) {
        log.warn(e.getMessage());
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(e.getErrorResponse());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(NotFoundException e) {
        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(e.getErrorResponse());
    }

    @ExceptionHandler(InvalidRangeException.class)
    public ResponseEntity<ErrorResponse> handle(InvalidRangeException e) {
        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(e.getErrorResponse());
    }

    @ExceptionHandler(LimitViolationException.class)
    public ResponseEntity<ErrorResponse> handle(LimitViolationException e) {
        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(e.getErrorResponse());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public String handle(HttpRequestMethodNotSupportedException e) {
        log.warn(e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handle(InternalServerException e) {
        log.error(e.getMessage(), e.getCause());
        return e.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handle(Exception e) {
        log.error("Server error occurred", e);
        return "Server error";
    }
}
