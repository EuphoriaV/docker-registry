package com.euphoriav.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class TokenResponse(
    val token: String,
    @field:JsonProperty("expires_in")
    val expiresIn: Long,
    @field:JsonProperty("issued_at")
    val issuedAt: Instant
)