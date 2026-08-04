package com.euphoriav.docker.registry.controller;

import com.euphoriav.docker.registry.aop.annotation.Log;
import com.euphoriav.docker.registry.api.BlobsApi;
import com.euphoriav.docker.registry.logic.blob.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BlobsController implements BlobsApi {

    private final CancelBlobUploadOperation cancelBlobUploadOperation;
    private final CheckBlobExistsOperation checkBlobExistsOperation;
    private final CompleteBlobUploadOperation completeBlobUploadOperation;
    private final GetBlobOperation getBlobOperation;
    private final InitiateBlobUploadOperation initiateBlobUploadOperation;
    private final UploadBlobChunkOperation uploadBlobChunkOperation;
    private final NativeWebRequest nativeWebRequest;

    @Log
    @Override
    public ResponseEntity<Void> cancelBlobUpload(String name, UUID uuid) {
        cancelBlobUploadOperation.activate(name, uuid);
        return ResponseEntity.noContent()
                .header("Docker-Upload-UUID", uuid.toString())
                .build();
    }

    @Log
    @Override
    public ResponseEntity<Void> checkBlobExists(String name, String digest) {
        var size = checkBlobExistsOperation.activate(name, digest);
        return ResponseEntity.ok()
                .header("Docker-Content-Digest", digest)
                .header("Content-Length", String.valueOf(size))
                .header("Content-Type", "application/octet-stream")
                .build();
    }

    @Log
    @Override
    public ResponseEntity<Void> completeBlobUpload(String name, UUID uuid, String digest, String range, Resource body) {
        completeBlobUploadOperation.activate(name, uuid, digest, range, body, getContentLength());
        return ResponseEntity.created(URI.create("/v2/%s/blobs/%s".formatted(name, digest)))
                .header("Docker-Content-Digest", digest)
                .build();
    }

    @Log
    @Override
    public ResponseEntity<Resource> getBlob(String name, String digest) {
        var response = getBlobOperation.activate(name, digest);
        return ResponseEntity.ok()
                .header("Docker-Content-Digest", digest)
                .header("Content-Length", String.valueOf(response.size()))
                .body(response.resource());
    }

    @Log
    @Override
    public ResponseEntity<Void> initiateBlobUpload(String name) {
        var id = initiateBlobUploadOperation.activate(name);
        return ResponseEntity.accepted()
                .location(URI.create("/v2/%s/blobs/uploads/%s".formatted(name, id)))
                .header("Docker-Upload-UUID", id.toString())
                .header("Range", "0-0")
                .build();
    }

    @Log
    @Override
    public ResponseEntity<Void> uploadBlobChunk(String name, UUID uuid, Resource body, String range) {
        var lastByte = uploadBlobChunkOperation.activate(name, uuid, body, range, getContentLength());
        return ResponseEntity.accepted()
                .location(URI.create("/v2/%s/blobs/uploads/%s".formatted(name, uuid)))
                .header("Docker-Upload-UUID", uuid.toString())
                .header("Range", "0-%d".formatted(lastByte))
                .build();
    }

    private long getContentLength() {
        return ((HttpServletRequest) nativeWebRequest.getNativeRequest()).getContentLengthLong();
    }
}
