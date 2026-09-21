package com.euphoriav.auth.dao

import com.euphoriav.auth.aop.annotation.Log
import com.euphoriav.auth.model.User
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class UserDao(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    private val userMapper =
        RowMapper { rs, _ -> User(rs.getLong("id"), rs.getString("login"), rs.getString("password_hash")) }

    @Log(isDebug = true)
    fun find(login: String): User? {
        //language=PostgreSQL
        val sql = "select id, login, password_hash from auth.user where login = :login"
        val params = mapOf(
            "login" to login
        )
        return jdbcTemplate.query(sql, params, userMapper).firstOrNull()
    }

    @Log(isDebug = true)
    fun create(login: String, passwordHash: String) {
        //language=PostgreSQL
        val sql = "insert into auth.user(login, password_hash) values (:login, :passwordHash)"
        val params = mapOf(
            "login" to login,
            "passwordHash" to passwordHash
        )
        jdbcTemplate.update(sql, params)
    }
}