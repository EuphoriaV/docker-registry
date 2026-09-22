package com.euphoriav.auth.dto

import java.time.Instant

data class TokenResponse(val token: String, val expiresIn: Long, val issuedAt: Instant)