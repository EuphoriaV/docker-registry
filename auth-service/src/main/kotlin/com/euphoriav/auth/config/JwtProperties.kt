package com.euphoriav.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(val issuer: String, val expirationSeconds: Long)