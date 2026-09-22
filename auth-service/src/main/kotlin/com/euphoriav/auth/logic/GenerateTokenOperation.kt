package com.euphoriav.auth.logic

import com.euphoriav.auth.config.JwtProperties
import com.euphoriav.auth.dto.TokenResponse
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.security.PrivateKey
import java.time.Instant
import java.util.Date

@Component
class GenerateTokenOperation(private val jwtProperties: JwtProperties, private val privateKey: PrivateKey) {

    fun activate(login: String, service: String): TokenResponse {
        val issuedAt = Instant.now()
        val expiresAt = issuedAt.plusSeconds(jwtProperties.expirationSeconds)

        val token = Jwts.builder()
            .issuer(jwtProperties.issuer)
            .subject(login)
            .audience().add(service).and()
            .issuedAt(Date.from(issuedAt))
            .notBefore(Date.from(issuedAt))
            .expiration(Date.from(expiresAt))
            .claim("access", emptyList<Any>())
            .signWith(privateKey)
            .compact()

        return TokenResponse(token, jwtProperties.expirationSeconds, issuedAt)
    }
}