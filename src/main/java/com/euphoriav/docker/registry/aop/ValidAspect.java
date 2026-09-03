package com.euphoriav.docker.registry.aop;

import com.euphoriav.docker.registry.dto.ErrorResponse;
import com.euphoriav.docker.registry.exception.InvalidRequestException;
import com.euphoriav.docker.registry.logic.helper.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ValidAspect {

    private final RequestValidator requestValidator;

    @Before("@annotation(com.euphoriav.docker.registry.aop.annotation.ValidName) && args(name, ..)")
    public void validateRepositoryName(String name) {
        if (!requestValidator.validateName(name)) {
            throw new InvalidRequestException("invalid repository name", ErrorResponse.ErrorCode.NAME_INVALID);
        }
    }
}
