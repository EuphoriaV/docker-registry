package com.euphoriav.docker.registry.model;

import lombok.Data;

import java.util.UUID;

@Data
public class BlobUpload {
    private UUID id;
    private String repository;
    private long bytesReceived;
}
