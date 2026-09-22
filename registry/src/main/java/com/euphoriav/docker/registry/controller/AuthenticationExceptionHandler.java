package com.euphoriav.docker.registry.controller;

import com.euphoriav.docker.registry.dto.ErrorResponse;
import com.euphoriav.docker.registry.enums.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Value("${app.auth.realm}")
    private String realm;

    @Value("${spring.application.name}")
    private String appName;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("authentication exception %s %s".formatted(request.getMethod(), request.getRequestURL()), authException);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, String.format("Bearer realm=\"%s\",service=\"%s\"", realm, appName));
        response.setContentType("application/json");
        var error = new ErrorResponse.ErrorDto(ErrorCode.UNAUTHORIZED, "authentication required", authException.getMessage());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(response.getOutputStream(), error);
    }
}
