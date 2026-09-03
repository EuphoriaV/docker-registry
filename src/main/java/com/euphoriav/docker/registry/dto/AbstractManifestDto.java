package com.euphoriav.docker.registry.dto;

import lombok.Data;

@Data
public abstract class AbstractManifestDto {

    private int schemaVersion;
    private String mediaType;
}
