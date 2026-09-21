package com.euphoriav.auth.controller

import com.euphoriav.auth.aop.annotation.Log
import com.euphoriav.auth.dto.CreateUserRequest
import com.euphoriav.auth.logic.CreateUserOperation
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val createUserOperation: CreateUserOperation
) {

    @Log(logArgs = false)
    @PostMapping("/users")
    fun registerUser(@Valid @RequestBody createUserRequest: CreateUserRequest): ResponseEntity<Unit> {
        createUserOperation.activate(createUserRequest)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build()
    }
}