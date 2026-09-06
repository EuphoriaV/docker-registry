package com.euphoriav.docker.registry.logic.manifest;

import com.euphoriav.docker.registry.dao.ManifestDao;
import com.euphoriav.docker.registry.dao.TagDao;
import com.euphoriav.docker.registry.dto.ErrorResponse;
import com.euphoriav.docker.registry.exception.NotFoundException;
import com.euphoriav.docker.registry.logic.helper.DigestHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeleteImageManifestOperation {

    private final ManifestDao manifestDao;
    private final TagDao tagDao;
    private final DigestHelper digestHelper;

    @Transactional
    public void activate(String name, String reference) {
        var isDigest = digestHelper.isDigest(reference);
        if (isDigest) {
            var deleted = manifestDao.delete(name, reference);
            if (deleted < 1) {
                throw new NotFoundException("manifest unknown to registry", ErrorResponse.ErrorCode.MANIFEST_UNKNOWN);
            }
            tagDao.deleteByDigest(name, reference);
        } else {
            var deleted = tagDao.deleteByTag(name, reference);
            if (deleted < 1) {
                throw new NotFoundException("tag unknown to registry", ErrorResponse.ErrorCode.MANIFEST_UNKNOWN);
            }
        }
    }
}
