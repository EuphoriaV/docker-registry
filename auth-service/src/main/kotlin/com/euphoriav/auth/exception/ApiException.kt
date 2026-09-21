package com.euphoriav.auth.exception

import com.euphoriav.auth.dto.ErrorDto

open class ApiException(val errorDto: ErrorDto, cause: Throwable? = null) : RuntimeException(errorDto.message, cause) {

    constructor(message: String, cause: Throwable? = null) : this(ErrorDto(message, ""), cause)
}