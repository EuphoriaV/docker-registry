package com.euphoriav.docker.registry.exception;

public class LimitViolationException extends RuntimeException {
    public LimitViolationException(String message) {
        super(message);
    }
}
