package com.euphoriav.docker.registry.controller;

import com.euphoriav.docker.registry.aop.annotation.Log;
import com.euphoriav.docker.registry.api.ManifestsApi;
import com.euphoriav.docker.registry.logic.manifest.PutImageManifestOperation;
import com.euphoriav.docker.registry.model.GetImageManifest200Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class ManifestController implements ManifestsApi {

    private final PutImageManifestOperation putImageManifestOperation;
    private final NativeWebRequest nativeWebRequest;

    @Log
    @Override
    public ResponseEntity<Void> deleteImageManifest(String name, String reference) {
        return ManifestsApi.super.deleteImageManifest(name, reference);
    }

    @Log
    @Override
    public ResponseEntity<GetImageManifest200Response> getImageManifest(String name, String reference, String accept) {
        return ManifestsApi.super.getImageManifest(name, reference, accept);
    }

    @Log
    @Override
    public ResponseEntity<Void> headImageManifest(String name, String reference, String accept) {
        return ManifestsApi.super.headImageManifest(name, reference, accept);
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
