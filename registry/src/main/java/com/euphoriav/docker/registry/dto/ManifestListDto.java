package com.euphoriav.docker.registry.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ManifestListDto extends AbstractManifestDto {

    private List<ManifestRefDto> manifests;

    @Data
    public static class ManifestRefDto {
        private String mediaType;
        private long size;
        private String digest;
        private PlatformDto platform;
    }

    @Data
    public static class PlatformDto {
        private String architecture;
        private String os;
        private String variant;
    }
}
