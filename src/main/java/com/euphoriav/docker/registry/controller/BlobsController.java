package com.euphoriav.docker.registry.controller;

import com.euphoriav.docker.registry.api.BlobsApi;
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

    private final InitiateBlobUploadOperation initiateBlobUploadOperation;
    private final UploadBlobChunkOperation uploadBlobChunkOperation;
    private final NativeWebRequest nativeWebRequest;

    @Override
    public ResponseEntity<Void> cancelBlobUpload(String name, String uuid) {
        return BlobsApi.super.cancelBlobUpload(name, uuid);
    }

    @Override
    public ResponseEntity<Void> checkBlobExists(String name, String digest) {
        return BlobsApi.super.checkBlobExists(name, digest);
    }

    @Override
    public ResponseEntity<Void> completeBlobUpload(String name, String uuid, String digest, Resource body) {
        return BlobsApi.super.completeBlobUpload(name, uuid, digest, body);
    }

    @Override
    public ResponseEntity<Resource> getBlob(String name, String digest) {
        return BlobsApi.super.getBlob(name, digest);
    }

    @Override
    public ResponseEntity<Void> getBlobUploadStatus(String name, String uuid) {
        return BlobsApi.super.getBlobUploadStatus(name, uuid);
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
    public ResponseEntity<Void> uploadBlobChunk(String name, UUID id, Resource body, String range) {
        log.info("uploadBlobChunk request {} {} {}", name, id, range);
        var lastByte = uploadBlobChunkOperation.activate(name, id, body, range, ((HttpServletRequest) nativeWebRequest.getNativeRequest()).getContentLengthLong());
        return ResponseEntity.accepted()
                .location(URI.create("/v2/%s/blobs/uploads/%s".formatted(name, id)))
                .header("Docker-Upload-UUID", id.toString())
                .header("Range", "0-%d".formatted(lastByte))
                .build();
    }
}
