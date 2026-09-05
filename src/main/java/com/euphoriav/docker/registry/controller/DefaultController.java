package com.euphoriav.docker.registry.controller;

import com.euphoriav.docker.registry.aop.annotation.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/")
public class DefaultController {

    @Log
    @GetMapping
    public ResponseEntity<Void> checkApiVersion() {
        return ResponseEntity.ok().build();
    }
}
