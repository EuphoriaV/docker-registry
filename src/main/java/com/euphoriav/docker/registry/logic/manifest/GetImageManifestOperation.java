package com.euphoriav.docker.registry.logic.manifest;

import com.euphoriav.docker.registry.dao.ManifestDao;
import com.euphoriav.docker.registry.enums.ErrorCode;
import com.euphoriav.docker.registry.exception.NotFoundException;
import com.euphoriav.docker.registry.logic.helper.DigestHelper;
import com.euphoriav.docker.registry.model.Manifest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetImageManifestOperation {

    private final ManifestDao manifestDao;
    private final DigestHelper digestHelper;

    public Manifest activate(String name, String reference) {
        var isDigest = digestHelper.isDigest(reference);
        Optional<Manifest> manifestOptional;
        if (isDigest) {
            manifestOptional = manifestDao.findByDigest(name, reference);
        } else {
            manifestOptional = manifestDao.findByTag(name, reference);
        }
        return manifestOptional.orElseThrow(() -> new NotFoundException("manifest unknown to registry", ErrorCode.MANIFEST_UNKNOWN));
    }
}
