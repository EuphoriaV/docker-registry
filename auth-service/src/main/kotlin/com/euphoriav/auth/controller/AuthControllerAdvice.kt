package com.euphoriav.auth.controller

import com.euphoriav.auth.dto.ErrorDto
import com.euphoriav.auth.exception.InvalidRequestException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AuthControllerAdvice {

    @ExceptionHandler(InvalidRequestException::class)
    fun handle(e: InvalidRequestException): ResponseEntity<ErrorDto> = ResponseEntity.badRequest().body(e.errorDto)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(e: MethodArgumentNotValidException): ResponseEntity<ErrorDto> =
        ResponseEntity.badRequest().body(ErrorDto("invalid argument", e.message))
}