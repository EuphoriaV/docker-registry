package com.euphoriav.docker.registry.logic.manifest.validator;

import com.euphoriav.docker.registry.dao.BlobDao;
import com.euphoriav.docker.registry.dto.ErrorResponse;
import com.euphoriav.docker.registry.dto.SimpleManifestDto;
import com.euphoriav.docker.registry.exception.InvalidRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SimpleManifestValidator extends AbstractManifestValidator<SimpleManifestDto> {

    private final BlobDao blobDao;

    public SimpleManifestValidator(ObjectMapper objectMapper, BlobDao blobDao) {
        super(objectMapper);
        this.blobDao = blobDao;
    }

    @Override
    public Set<String> getSupportedMediaTypes() {
        return Set.of(
                "application/vnd.docker.distribution.manifest.v2+json",
                "application/vnd.oci.image.manifest.v1+json"
        );
    }

    @Override
    public Class<SimpleManifestDto> getSupportedClass() {
        return SimpleManifestDto.class;
    }

    @Override
    public void validate(String name, String contentType, SimpleManifestDto manifest) {
        if (manifest.getConfig() == null || manifest.getLayers() == null) {
            throw new InvalidRequestException("manifest contains null config or layers", ErrorResponse.ErrorCode.MANIFEST_INVALID);
        }
        if (blobDao.find(manifest.getConfig().getDigest(), name).isEmpty()) {
            throw new InvalidRequestException("manifest references a blob unknown to registry", ErrorResponse.ErrorCode.MANIFEST_BLOB_UNKNOWN);
        }
        manifest.getLayers().forEach(blobRef -> {
            if (blobDao.find(blobRef.getDigest(), name).isEmpty()) {
                throw new InvalidRequestException("manifest references a blob unknown to registry", ErrorResponse.ErrorCode.MANIFEST_BLOB_UNKNOWN);
            }
        });
    }
}
