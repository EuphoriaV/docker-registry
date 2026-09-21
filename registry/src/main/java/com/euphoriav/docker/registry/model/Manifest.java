package com.euphoriav.docker.registry.model;

import lombok.Data;

@Data
public class Manifest {
    private long id;
    private String repository;
    private String digest;
    private byte[] data;
    private String mediaType;
    private long size;
}
