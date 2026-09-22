package com.euphoriav.auth.config

import com.euphoriav.auth.controller.AuthenticationExceptionHandler
import com.euphoriav.auth.dao.UserDao
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

@EnableConfigurationProperties(JwtProperties::class)
@Configuration
class SecurityConfiguration {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        authenticationExceptionHandler: AuthenticationExceptionHandler,
    ): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize("/swagger-ui/**", permitAll)
                authorize("/swagger-ui.html", permitAll)
                authorize("/v3/api-docs/**", permitAll)
                authorize(HttpMethod.POST, "/users", permitAll)
                authorize(anyRequest, authenticated)
            }
            exceptionHandling {
                authenticationEntryPoint = authenticationExceptionHandler
            }
            csrf { disable() }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            httpBasic {}
        }
        return http.build()
    }

    @Bean
    fun userDetailsService(userDao: UserDao) = UserDetailsService { u ->
        userDao.find(u)
            ?.let { User(it.login, it.passwordHash, emptyList()) }
            ?: throw UsernameNotFoundException("user $u not found")
    }

    @Bean
    fun jwtPrivateKey(@Value("classpath:jwt/jwt.pem") privateKey: Resource): PrivateKey {
        val privateKeyBytes = privateKey.inputStream.bufferedReader().readText()
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s".toRegex(), "")
            .let { Base64.getDecoder().decode(it) }

        return KeyFactory.getInstance("RSA")
            .generatePrivate(PKCS8EncodedKeySpec(privateKeyBytes))
    }
}