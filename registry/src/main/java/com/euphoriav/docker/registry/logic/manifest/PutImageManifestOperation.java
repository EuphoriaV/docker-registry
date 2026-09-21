package com.euphoriav.docker.registry.logic.manifest;

import com.euphoriav.docker.registry.dao.ManifestDao;
import com.euphoriav.docker.registry.dao.TagDao;
import com.euphoriav.docker.registry.enums.DigestAlgorithm;
import com.euphoriav.docker.registry.enums.ErrorCode;
import com.euphoriav.docker.registry.exception.InternalServerException;
import com.euphoriav.docker.registry.exception.InvalidRequestException;
import com.euphoriav.docker.registry.exception.LimitViolationException;
import com.euphoriav.docker.registry.logic.helper.DigestHelper;
import com.euphoriav.docker.registry.logic.manifest.validator.AbstractManifestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


@Component
public class PutImageManifestOperation {

    private static final Pattern TAG_PATTERN = Pattern.compile("[a-zA-Z0-9_][a-zA-Z0-9_.-]{0,127}");
    private static final Integer MANIFEST_MAX_SIZE_BYTES = 4 * 1024 * 1024;

    private final DigestHelper digestHelper;
    private final Map<String, AbstractManifestValidator<?>> manifestValidators;
    private final ManifestDao manifestDao;
    private final TagDao tagDao;

    @Lazy
    @Autowired
    private PutImageManifestOperation self;

    public PutImageManifestOperation(DigestHelper digestHelper, List<AbstractManifestValidator<?>> manifestValidators, ManifestDao manifestDao, TagDao tagDao) {
        this.digestHelper = digestHelper;
        this.manifestDao = manifestDao;
        this.tagDao = tagDao;
        this.manifestValidators = new HashMap<>();
        manifestValidators.forEach(validator -> {
            validator.getSupportedMediaTypes().forEach(mediaType -> {
                this.manifestValidators.put(mediaType, validator);
            });
        });
    }

    public String activate(String name, String reference, Resource resource, String contentType) {
        var isDigest = digestHelper.isDigest(reference);
        var digestAlgorithm = isDigest ? DigestAlgorithm.fromDigest(reference) : DigestAlgorithm.SHA_256;

        String digest;
        byte[] data;
        try {
            data = resource.getContentAsByteArray();
            digest = digestHelper.calculateDigest(new ByteArrayInputStream(data), digestAlgorithm);
        } catch (Exception e) {
            throw new InternalServerException("could not calculate actual digest", e);
        }

        self.process(name, reference, contentType, data, digest, isDigest);
        return digest;
    }

    @Transactional
    public void process(String name, String reference, String contentType, byte[] data, String digest, boolean isDigest) {
        if (data.length > MANIFEST_MAX_SIZE_BYTES) {
            throw new LimitViolationException("manifest is too large");
        }

        if (isDigest) {
            if (!digest.equals(reference)) {
                throw new InvalidRequestException("provided digest did not match uploaded content", ErrorCode.DIGEST_INVALID);
            }
        } else if (!TAG_PATTERN.matcher(reference).matches()) {
            throw new InvalidRequestException("invalid tag", ErrorCode.MANIFEST_INVALID);
        }

        var validator = manifestValidators.get(contentType);
        if (validator == null) {
            throw new InvalidRequestException("unsupported media type", ErrorCode.MANIFEST_INVALID);
        }
        validator.validate(name, contentType, data);

        manifestDao.create(name, digest, data, contentType);
        if (!isDigest) {
            tagDao.create(name, digest, reference);
        }
    }
}
