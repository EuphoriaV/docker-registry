package com.euphoriav.docker.registry.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class LoggingAspect {

    @Before("@annotation(com.euphoriav.docker.registry.aop.annotation.Log)")
    public void logMethodArgs(JoinPoint jp) {
        var logger = LoggerFactory.getLogger(jp.getTarget().getClass());
        var method = jp.getSignature().getName();
        var args = Arrays.stream(jp.getArgs())
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        logger.info("{}({})", method, args);
    }
}
