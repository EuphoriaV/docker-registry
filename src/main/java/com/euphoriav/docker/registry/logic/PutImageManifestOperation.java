package com.euphoriav.docker.registry.logic;

import com.euphoriav.docker.registry.dao.RepositoryDao;
import com.euphoriav.docker.registry.exception.InvalidRequestException;
import com.euphoriav.docker.registry.exception.LimitViolationException;
import com.euphoriav.docker.registry.exception.NotFoundException;
import com.euphoriav.docker.registry.model.PutImageManifestRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.security.MessageDigest;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class PutImageManifestOperation {

    private final RepositoryDao repositoryDao;

    private static final String EXPECTED_CONTENT_TYPE = "application/vnd.docker.distribution.manifest.v2+json";
    private static final Integer EXPECTED_SCHEMA_VERSION = 2;
    private static final Integer MAX_MANIFEST_BYTES = 4 * 1024 * 1024;
    private static final Pattern DIGEST_PATTERN = Pattern.compile("sha256:[a-f0-9]{64}");
    private static final Pattern TAG_PATTERN = Pattern.compile("[a-zA-Z0-9_][a-zA-Z0-9._-]{0,127}");

    public String activate(String name, String reference, PutImageManifestRequest request, HttpServletRequest httpRequest) {
        var body = ((ContentCachingRequestWrapper) httpRequest).getContentAsByteArray();
        validateRequest(name, reference, request, body);
        var digest = calcDigest(body);
        if (isDigest(reference)) {
            if (!digest.equals(reference)) {
                throw new InvalidRequestException("Reference does not match calculated digest");
            }
        } else {
            if(!TAG_PATTERN.matcher(reference).matches()){
                throw new InvalidRequestException("Invalid tag name");
            }
        }
        return digest;
    }

    private void validateRequest(String name, String reference, PutImageManifestRequest request, byte[] body) {
        if (!EXPECTED_CONTENT_TYPE.equals(request.getMediaType())) {
            throw new InvalidRequestException("Invalid mediaType, expected %s".formatted(EXPECTED_CONTENT_TYPE));
        }
        if (!EXPECTED_SCHEMA_VERSION.equals(request.getSchemaVersion())) {
            throw new InvalidRequestException("Invalid schemaVersion, expected %s".formatted(EXPECTED_SCHEMA_VERSION));
        }
        if (!isDigest(request.getConfig().getDigest())) {
            throw new InvalidRequestException("Invalid config.digest");
        }
        for (int i = 0; i < request.getLayers().size(); i++) {
            if (!isDigest(request.getLayers().get(i).getDigest())) {
                throw new InvalidRequestException("Invalid layers[%s].digest".formatted(i));
            }
        }
        if (body.length > MAX_MANIFEST_BYTES) {
            throw new LimitViolationException("Request body exceeds limit of %s bytes".formatted(MAX_MANIFEST_BYTES));
        }
        //TODO: verify blobs exists
        if (!repositoryDao.existsByName(name)) {
            throw new NotFoundException("Repository with given name does not exist");
        }
    }

    private boolean isDigest(String s) {
        return DIGEST_PATTERN.matcher(s).matches();
    }

    @SneakyThrows
    private String calcDigest(byte[] body) {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(body);
        StringBuilder sb = new StringBuilder("sha256:");
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
