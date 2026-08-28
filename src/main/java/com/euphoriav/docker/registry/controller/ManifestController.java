package com.euphoriav.docker.registry.controller;

import com.euphoriav.docker.registry.aop.annotation.Log;
import com.euphoriav.docker.registry.api.ManifestsApi;
import com.euphoriav.docker.registry.logic.manifest.GetImageManifestOperation;
import com.euphoriav.docker.registry.logic.manifest.PutImageManifestOperation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class ManifestController implements ManifestsApi {

    private final GetImageManifestOperation getImageManifestOperation;
    private final PutImageManifestOperation putImageManifestOperation;
    private final NativeWebRequest nativeWebRequest;

    @Log
    @Override
    public ResponseEntity<Resource> getImageManifest(String name, String reference) {
        var manifest = getImageManifestOperation.activate(name, reference);
        return ResponseEntity.ok()
                .header("Docker-Content-Digest", manifest.getDigest())
                .header("Content-Length", String.valueOf(manifest.getSize()))
                .header("Content-Type", manifest.getMediaType())
                .body(new ByteArrayResource(manifest.getData()));
    }

    @Log
    @Override
    public ResponseEntity<Void> headImageManifest(String name, String reference) {
        var manifest = getImageManifestOperation.activate(name, reference);
        return ResponseEntity.ok()
                .header("Docker-Content-Digest", manifest.getDigest())
                .header("Content-Length", String.valueOf(manifest.getSize()))
                .header("Content-Type", manifest.getMediaType())
                .build();
    }

    @Log
    @Override
    public ResponseEntity<Void> putImageManifest(String name, String reference, Resource resource) {
        var digest = putImageManifestOperation.activate(name, reference, resource, getContentType());
        return ResponseEntity.created(URI.create("/v2/%s/manifests/%s".formatted(name, reference)))
                .header("Docker-Content-Digest", digest)
                .header("Content-Length", "0")
                .build();
    }

    private String getContentType() {
        return ((HttpServletRequest) nativeWebRequest.getNativeRequest()).getContentType();
    }
}
