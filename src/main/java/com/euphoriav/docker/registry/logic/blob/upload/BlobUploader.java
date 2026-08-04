package com.euphoriav.docker.registry.logic.blob.upload;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public interface BlobUploader {
    void initUpload(UUID id) throws IOException;

    void delete(UUID id);

    void uploadChunk(UUID id, InputStream inputStream, long offset) throws IOException;

    String computeDigest(UUID id) throws NoSuchAlgorithmException, IOException;

    InputStream getInputStream(String filename) throws IOException;
}
