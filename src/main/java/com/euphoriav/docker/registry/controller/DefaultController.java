package com.euphoriav.docker.registry.controller;

import com.euphoriav.docker.registry.aop.annotation.Log;
import com.euphoriav.docker.registry.api.DefaultApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController implements DefaultApi {

    @Log
    @Override
    public ResponseEntity<Void> checkApiVersion() {
        return ResponseEntity.ok().build();
    }
}
