package com.euphoriav.docker.registry.controller;

import com.euphoriav.docker.registry.api.ManifestsApi;
import com.euphoriav.docker.registry.logic.PutImageManifestOperation;
import com.euphoriav.docker.registry.model.GetImageManifest200Response;
import com.euphoriav.docker.registry.model.PutImageManifestRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ManifestController implements ManifestsApi {

    private final NativeWebRequest nativeWebRequest;
    private final PutImageManifestOperation putImageManifestOperation;

    @Override
    public ResponseEntity<Void> deleteImageManifest(String name, String reference) {
        return ManifestsApi.super.deleteImageManifest(name, reference);
    }

    @Override
    public ResponseEntity<GetImageManifest200Response> getImageManifest(String name, String reference, String accept) {
        return ManifestsApi.super.getImageManifest(name, reference, accept);
    }

    @Override
    public ResponseEntity<Void> headImageManifest(String name, String reference, String accept) {
        return ManifestsApi.super.headImageManifest(name, reference, accept);
    }

    @Override
    public ResponseEntity<Void> putImageManifest(String name, String reference, PutImageManifestRequest request) {
        log.info("putImageManifest request {}, {}, {}", name, reference, request);
        var digest = putImageManifestOperation.activate(name, reference, request, (HttpServletRequest) nativeWebRequest.getNativeRequest());
        return ResponseEntity.created(URI.create("/v2/%s/manifests/%s".formatted(name, reference)))
                .header("Docker-Content-Digest", digest)
                .build();
    }
}
