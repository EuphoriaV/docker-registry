package com.euphoriav.docker.registry.controller;

import com.euphoriav.docker.registry.aop.annotation.Log;
import com.euphoriav.docker.registry.aop.annotation.Name;
import com.euphoriav.docker.registry.logic.manifest.GetImageManifestOperation;
import com.euphoriav.docker.registry.logic.manifest.PutImageManifestOperation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/**/manifests/{reference}")
public class ManifestController {

    private final GetImageManifestOperation getImageManifestOperation;
    private final PutImageManifestOperation putImageManifestOperation;
    private final NativeWebRequest nativeWebRequest;

    @Log
    @GetMapping(produces = {"application/vnd.docker.distribution.manifest.v2+json", "application/vnd.docker.distribution.manifest.list.v2+json", "application/vnd.oci.image.manifest.v1+json", "application/vnd.oci.image.index.v1+json"})
    public ResponseEntity<Resource> getImageManifest(@Name String name, @PathVariable("reference") String reference) {
        var manifest = getImageManifestOperation.activate(name, reference);
        return ResponseEntity.ok()
                .header("Docker-Content-Digest", manifest.getDigest())
                .header("Content-Length", String.valueOf(manifest.getSize()))
                .header("Content-Type", manifest.getMediaType())
                .body(new ByteArrayResource(manifest.getData()));
    }

    @Log
    @RequestMapping(method = RequestMethod.HEAD)
    public ResponseEntity<Void> headImageManifest(@Name String name, @PathVariable("reference") String reference) {
        var manifest = getImageManifestOperation.activate(name, reference);
        return ResponseEntity.ok()
                .header("Docker-Content-Digest", manifest.getDigest())
                .header("Content-Length", String.valueOf(manifest.getSize()))
                .header("Content-Type", manifest.getMediaType())
                .build();
    }

    @Log
    @PutMapping(consumes = {"application/vnd.docker.distribution.manifest.v2+json", "application/vnd.docker.distribution.manifest.list.v2+json", "application/vnd.oci.image.manifest.v1+json", "application/vnd.oci.image.index.v1+json"})
    public ResponseEntity<Void> putImageManifest(@Name String name, @PathVariable("reference") String reference, @RequestBody Resource resource) {
        var digest = putImageManifestOperation.activate(name, reference, resource, getContentType());
        return ResponseEntity.created(URI.create("/v2/%s/manifests/%s".formatted(name, reference)))
                .header("Docker-Content-Digest", digest)
                .header("Content-Length", "0")
                .build();
    }

    private String getContentType() {
        return nativeWebRequest.getNativeRequest(HttpServletRequest.class).getContentType();
    }
}
