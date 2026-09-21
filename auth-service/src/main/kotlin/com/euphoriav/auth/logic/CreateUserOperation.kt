package com.euphoriav.auth.logic

import com.euphoriav.auth.dao.UserDao
import com.euphoriav.auth.dto.CreateUserRequest
import com.euphoriav.auth.exception.InvalidRequestException
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class CreateUserOperation(private val userDao: UserDao, private val passwordEncoder: PasswordEncoder) {

    fun activate(createUserRequest: CreateUserRequest) {
        val login = createUserRequest.login
        if (userDao.find(login) != null) {
            throw InvalidRequestException("user $login already exists")
        }

        try {
            userDao.create(login, passwordEncoder.encode(createUserRequest.password))
        } catch (_: DuplicateKeyException) {
            throw InvalidRequestException("user $login already exists")
        }
    }
}