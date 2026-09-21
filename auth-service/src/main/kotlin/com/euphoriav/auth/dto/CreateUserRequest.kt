package com.euphoriav.auth.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateUserRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 50)
    val login: String,
    @field:NotBlank
    @field:Size(min = 4, max = 50)
    val password: String,
)
