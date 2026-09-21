package com.euphoriav.docker.registry.controller;

import com.euphoriav.docker.registry.aop.annotation.Log;
import com.euphoriav.docker.registry.aop.annotation.Name;
import com.euphoriav.docker.registry.logic.blob.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/**/blobs/")
public class BlobsController {

    private final InitiateBlobUploadOperation initiateBlobUploadOperation;
    private final GetBlobUploadStatusOperation getBlobUploadStatusOperation;
    private final UploadBlobChunkOperation uploadBlobChunkOperation;
    private final CancelBlobUploadOperation cancelBlobUploadOperation;
    private final CompleteBlobUploadOperation completeBlobUploadOperation;
    private final CheckBlobExistsOperation checkBlobExistsOperation;
    private final GetBlobOperation getBlobOperation;
    private final DeleteBlobOperation deleteBlobOperation;

    @Log
    @PostMapping("/uploads/")
    public ResponseEntity<Void> initiateBlobUpload(@Name String name) {
        var id = initiateBlobUploadOperation.activate(name);
        return ResponseEntity.accepted()
                .location(URI.create("/v2/%s/blobs/uploads/%s".formatted(name, id)))
                .header("Docker-Upload-UUID", id.toString())
                .header("Range", "0-0")
                .build();
    }

    @Log
    @GetMapping("/uploads/{uuid}")
    public ResponseEntity<Void> getBlobUploadStatus(@Name String name, @PathVariable("uuid") UUID uuid) {
        var lastByte = getBlobUploadStatusOperation.activate(name, uuid);
        return ResponseEntity.noContent()
                .location(URI.create("/v2/%s/blobs/uploads/%s".formatted(name, uuid)))
                .header("Docker-Upload-UUID", uuid.toString())
                .header("Range", "0-%d".formatted(lastByte - 1))
                .build();
    }

    @Log
    @PatchMapping(
            value = "/uploads/{uuid}",
            consumes = {"application/octet-stream"}
    )
    public ResponseEntity<Void> uploadBlobChunk(@Name String name, @PathVariable("uuid") UUID uuid, @RequestBody Resource body,
                                                @RequestHeader(value = "Content-Range", required = false) String range) {
        var lastByte = uploadBlobChunkOperation.activate(name, uuid, body, range);
        return ResponseEntity.accepted()
                .location(URI.create("/v2/%s/blobs/uploads/%s".formatted(name, uuid)))
                .header("Docker-Upload-UUID", uuid.toString())
                .header("Range", "0-%d".formatted(lastByte))
                .build();
    }

    @Log
    @DeleteMapping("/uploads/{uuid}")
    public ResponseEntity<Void> cancelBlobUpload(@Name String name, @PathVariable("uuid") UUID uuid) {
        cancelBlobUploadOperation.activate(name, uuid);
        return ResponseEntity.noContent()
                .header("Docker-Upload-UUID", uuid.toString())
                .build();
    }

    @Log
    @PutMapping(
            value = "/uploads/{uuid}",
            consumes = {"application/octet-stream"}
    )
    public ResponseEntity<Void> completeBlobUpload(@Name String name, @PathVariable("uuid") UUID uuid, @RequestParam String digest,
                                                   @RequestHeader(value = "Content-Range", required = false) String range,
                                                   @RequestBody(required = false) Resource body) {
        completeBlobUploadOperation.activate(name, uuid, digest, range, body);
        return ResponseEntity.created(URI.create("/v2/%s/blobs/%s".formatted(name, digest)))
                .header("Docker-Content-Digest", digest)
                .build();
    }

    @Log
    @RequestMapping(
            method = RequestMethod.HEAD,
            value = "/{digest}"
    )
    public ResponseEntity<Void> checkBlobExists(@Name String name, @PathVariable("digest") String digest) {
        var size = checkBlobExistsOperation.activate(name, digest);
        return ResponseEntity.ok()
                .header("Docker-Content-Digest", digest)
                .header("Content-Length", String.valueOf(size))
                .header("Content-Type", "application/octet-stream")
                .build();
    }

    @Log
    @GetMapping(
            value = "/{digest}",
            produces = {"application/octet-stream"}
    )
    public ResponseEntity<Resource> getBlob(@Name String name, @PathVariable("digest") String digest) {
        var response = getBlobOperation.activate(name, digest);
        return ResponseEntity.ok()
                .header("Docker-Content-Digest", digest)
                .header("Content-Length", String.valueOf(response.size()))
                .body(response.resource());
    }

    @Log
    @DeleteMapping("/{digest}")
    public ResponseEntity<Void> deleteBlob(@Name String name, @PathVariable("digest") String digest) {
        deleteBlobOperation.activate(name, digest);
        return ResponseEntity.accepted().build();
    }
}
