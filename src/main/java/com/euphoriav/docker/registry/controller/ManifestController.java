package com.euphoriav.docker.registry.controller;

import com.euphoriav.docker.registry.api.ManifestsApi;
import com.euphoriav.docker.registry.model.GetImageManifest200Response;
import com.euphoriav.docker.registry.model.PutImageManifestRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManifestController implements ManifestsApi {

    @Override
    public ResponseEntity<Void> deleteImageManifest(String name, String reference, String authorization) {
        return ManifestsApi.super.deleteImageManifest(name, reference, authorization);
    }

    @Override
    public ResponseEntity<GetImageManifest200Response> getImageManifest(String name, String reference, String authorization, String accept) {
        return ManifestsApi.super.getImageManifest(name, reference, authorization, accept);
    }

    @Override
    public ResponseEntity<Void> headImageManifest(String name, String reference, String authorization, String accept) {
        return ManifestsApi.super.headImageManifest(name, reference, authorization, accept);
    }

    @Override
    public ResponseEntity<Void> putImageManifest(String name, String reference, String authorization, String contentType, PutImageManifestRequest putImageManifestRequest) {
        return ManifestsApi.super.putImageManifest(name, reference, authorization, contentType, putImageManifestRequest);
    }
}
