package com.euphoriav.docker.registry.logic.blob.upload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Slf4j
@Component
public class LocalBlobUploader implements BlobUploader {

    private static final Path UPLOADS_PATH = Path.of("uploads");

    @Override
    public void initUpload(UUID id) throws IOException {
        var filePath = UPLOADS_PATH.resolve(id.toString());
        Files.createFile(filePath);
        log.info("Created empty file {}", filePath);
    }

    @Override
    public void uploadChunk(UUID id, InputStream inputStream) throws IOException {
        var filePath = UPLOADS_PATH.resolve(id.toString());
        try (OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.APPEND)) {
            inputStream.transferTo(outputStream);
        }
        log.info("Upload data to file {}", filePath);
    }
}
