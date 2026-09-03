package com.euphoriav.docker.registry.logic.manifest.validator;

import com.euphoriav.docker.registry.dao.ManifestDao;
import com.euphoriav.docker.registry.dto.ErrorResponse;
import com.euphoriav.docker.registry.dto.ManifestListDto;
import com.euphoriav.docker.registry.exception.InvalidRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ManifestListValidator extends AbstractManifestValidator<ManifestListDto> {

    private final ManifestDao manifestDao;

    public ManifestListValidator(ObjectMapper objectMapper, ManifestDao manifestDao) {
        super(objectMapper);
        this.manifestDao = manifestDao;
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
        if (manifest.getManifests() == null) {
            throw new InvalidRequestException("manifests section is null", ErrorResponse.ErrorCode.MANIFEST_INVALID);
        }
        manifest.getManifests().forEach(manifestRef -> {
            if (manifestDao.findByDigest(name, manifestRef.getDigest()).isEmpty()) {
                throw new InvalidRequestException("manifest references a manifest unknown to registry", ErrorResponse.ErrorCode.MANIFEST_BLOB_UNKNOWN);
            }
        });
    }
}