package com.euphoriav.docker.registry.controller;

import com.euphoriav.docker.registry.api.BlobsApi;
import com.euphoriav.docker.registry.logic.blob.CheckBlobExistsOperation;
import com.euphoriav.docker.registry.logic.blob.CompleteBlobUploadOperation;
import com.euphoriav.docker.registry.logic.blob.InitiateBlobUploadOperation;
import com.euphoriav.docker.registry.logic.blob.UploadBlobChunkOperation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BlobsController implements BlobsApi {

    private final CheckBlobExistsOperation checkBlobExistsOperation;
    private final CompleteBlobUploadOperation completeBlobUploadOperation;
    private final InitiateBlobUploadOperation initiateBlobUploadOperation;
    private final UploadBlobChunkOperation uploadBlobChunkOperation;
    private final NativeWebRequest nativeWebRequest;

    @Override
    public ResponseEntity<Void> cancelBlobUpload(String name, String uuid) {
        return BlobsApi.super.cancelBlobUpload(name, uuid);
    }

    @Override
    public ResponseEntity<Void> checkBlobExists(String name, String digest) {
        log.info("checkBlobExists request {} {}", name, digest);
        var size = checkBlobExistsOperation.activate(name, digest);
        return ResponseEntity.ok()
                .header("Docker-Content-Digest", digest)
                .header("Content-Length", String.valueOf(size))
                .header("Content-Type", "application/octet-stream")
                .build();
    }

    @Override
    public ResponseEntity<Void> completeBlobUpload(String name, UUID uuid, String digest, String range, Resource body) {
        log.info("completeBlobUpload request {} {} {} {}", name, uuid, digest, range);
        completeBlobUploadOperation.activate(name, uuid, digest, range, body, ((HttpServletRequest) nativeWebRequest.getNativeRequest()).getContentLengthLong());
        return ResponseEntity.created(URI.create("/v2/%s/blobs/%s".formatted(name, digest)))
                .header("Docker-Content-Digest", digest)
                .build();
    }

    @Override
    public ResponseEntity<Resource> getBlob(String name, String digest) {
        return BlobsApi.super.getBlob(name, digest);
    }

    @Override
    public ResponseEntity<Void> initiateBlobUpload(String name) {
        log.info("initiateBlobUpload request {}", name);
        var id = initiateBlobUploadOperation.activate(name);
        return ResponseEntity.accepted()
                .location(URI.create("/v2/%s/blobs/uploads/%s".formatted(name, id)))
                .header("Docker-Upload-UUID", id.toString())
                .header("Range", "0-0")
                .build();
    }

    @Override
    public ResponseEntity<Void> uploadBlobChunk(String name, UUID uuid, Resource body, String range) {
        log.info("uploadBlobChunk request {} {} {}", name, uuid, range);
        var lastByte = uploadBlobChunkOperation.activate(name, uuid, body, range, ((HttpServletRequest) nativeWebRequest.getNativeRequest()).getContentLengthLong());
        return ResponseEntity.accepted()
                .location(URI.create("/v2/%s/blobs/uploads/%s".formatted(name, uuid)))
                .header("Docker-Upload-UUID", uuid.toString())
                .header("Range", "0-%d".formatted(lastByte))
                .build();
    }
}
