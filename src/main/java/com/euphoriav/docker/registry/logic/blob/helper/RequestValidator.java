package com.euphoriav.docker.registry.logic.blob.helper;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class RequestValidator {

    private static final Pattern REPOSITORY_NAME_PATTERN = Pattern.compile("[a-z0-9]+((\\.|_|__|-+)[a-z0-9]+)*(/[a-z0-9]+((\\.|_|__|-+)[a-z0-9]+)*)*");
    private static final int REPOSITORY_NAME_MAX_LENGTH = 255;

    public boolean validateName(String name) {
        return name.length() <= REPOSITORY_NAME_MAX_LENGTH && REPOSITORY_NAME_PATTERN.matcher(name).matches();
    }
}
