package com.euphoriav.docker.registry.controller;

import com.euphoriav.docker.registry.api.DefaultApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DefaultController implements DefaultApi {

    @Override
    public ResponseEntity<Void> checkApiVersion() {
        log.info("checkApiVersion request");
        return ResponseEntity.ok().build();
    }
}
