package com.euphoriav.docker.registry.logic.manifest.validator;

import com.euphoriav.docker.registry.dao.BlobDao;
import com.euphoriav.docker.registry.dto.ManifestListDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ManifestListValidator extends AbstractManifestValidator<ManifestListDto> {

    private final BlobDao blobDao;

    public ManifestListValidator(ObjectMapper objectMapper, BlobDao blobDao) {
        super(objectMapper);
        this.blobDao = blobDao;
    }

    @Override
    public Set<String> getSupportedMediaTypes() {
        return Set.of(
                "application/vnd.docker.distribution.manifest.list.v2+json",
                "application/vnd.oci.image.index.v1+json"
        );
    }

    @Override
    public Class<ManifestListDto> getSupportedClass() {
        return ManifestListDto.class;
    }

    @Override
    public void validate(String name, String contentType, ManifestListDto manifest) {
    }
}