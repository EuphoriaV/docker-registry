package com.euphoriav.docker.registry.logic.manifest.validator;

import com.euphoriav.docker.registry.dto.AbstractManifestDto;
import com.euphoriav.docker.registry.dto.ErrorResponse;
import com.euphoriav.docker.registry.exception.InvalidRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Set;

@RequiredArgsConstructor
public abstract class AbstractManifestValidator<T extends AbstractManifestDto> {

    private final ObjectMapper objectMapper;

    public abstract Set<String> getSupportedMediaTypes();

    public abstract Class<T> getSupportedClass();

    public void validate(String name, String contentType, byte[] data) {
        T manifest;
        try {
            manifest = objectMapper.readValue(data, getSupportedClass());
        } catch (IOException e) {
            throw new InvalidRequestException("could not read manifest", ErrorResponse.ErrorCode.MANIFEST_INVALID, e.getMessage());
        }

        if (manifest.getSchemaVersion() != 2) {
            throw new InvalidRequestException("invalid schema version", ErrorResponse.ErrorCode.MANIFEST_INVALID);
        }

        if (!contentType.equals(manifest.getMediaType())) {
            throw new InvalidRequestException("media type of manifest does not match content-type", ErrorResponse.ErrorCode.MANIFEST_INVALID);
        }
        validate(name, contentType, manifest);
    }

    public abstract void validate(String name, String contentType, T manifest);
}
