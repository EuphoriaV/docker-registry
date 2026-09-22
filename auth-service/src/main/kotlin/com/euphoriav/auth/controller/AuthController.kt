package com.euphoriav.auth.controller

import com.euphoriav.auth.aop.annotation.Log
import com.euphoriav.auth.dto.CreateUserRequest
import com.euphoriav.auth.dto.TokenResponse
import com.euphoriav.auth.logic.CreateUserOperation
import com.euphoriav.auth.logic.GenerateTokenOperation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@SecurityRequirement(name = "basicAuth")
@RestController
class AuthController(
    private val createUserOperation: CreateUserOperation,
    private val generateTokenOperation: GenerateTokenOperation,
) {

    @Log(logArgs = false)
    @PostMapping("/users")
    fun registerUser(@Valid @RequestBody createUserRequest: CreateUserRequest): ResponseEntity<Unit> {
        createUserOperation.activate(createUserRequest)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build()
    }

    @Log(logArgs = false)
    @GetMapping("/token")
    fun getToken(
        principal: Principal,
        @RequestParam service: String,
        @RequestParam(required = false) scope: String?,
    ): ResponseEntity<TokenResponse> {
        val token = generateTokenOperation.activate(principal.name, service)
        return ResponseEntity.ok(token)
    }
}