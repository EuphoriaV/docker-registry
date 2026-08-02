package com.euphoriav.docker.registry.logic.blob.upload;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
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

    @SneakyThrows
    @Override
    public void delete(UUID id) {
        var filePath = UPLOADS_PATH.resolve(id.toString());
        Files.deleteIfExists(filePath);
        log.info("Deleted file {}", filePath);
    }

    @Override
    public void uploadChunk(UUID id, InputStream inputStream, long offset) throws IOException {
        var filePath = UPLOADS_PATH.resolve(id.toString());
        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.WRITE)) {
            channel.position(offset);
            try (OutputStream out = Channels.newOutputStream(channel)) {
                inputStream.transferTo(out);
            }
        }
        log.info("Upload data to file {}", filePath);
    }

    @Override
    public String computeDigest(UUID id) throws NoSuchAlgorithmException, IOException {
        var filePath = UPLOADS_PATH.resolve(id.toString());
        var digest = MessageDigest.getInstance("SHA-256");

        try (InputStream in = Files.newInputStream(filePath);
             DigestInputStream digestIn = new DigestInputStream(in, digest)) {
            digestIn.transferTo(OutputStream.nullOutputStream());
        }

        return "sha256:" + HexFormat.of().formatHex(digest.digest());
    }
}
