package com.euphoriav.docker.registry.model;

import lombok.Data;

@Data
public class Blob {
    private long id;
    private String repository;
    private String digest;
    private String filename;
    private long size;
}
