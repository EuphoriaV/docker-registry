package com.euphoriav.docker.registry.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SimpleManifestDto extends AbstractManifestDto{

    private BlobRefDto config;
    private List<BlobRefDto> layers;

    @Data
    public static class BlobRefDto {
        private String mediaType;
        private long size;
        private String digest;
    }
}
