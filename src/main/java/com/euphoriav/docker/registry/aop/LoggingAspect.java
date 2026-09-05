package com.euphoriav.docker.registry.aop;

import com.euphoriav.docker.registry.aop.annotation.Log;
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

    @Before("@annotation(log)")
    public void logMethodArgs(JoinPoint jp, Log log) {
        var logger = LoggerFactory.getLogger(jp.getTarget().getClass());
        var method = jp.getSignature().getName();
        var args = Arrays.stream(jp.getArgs())
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        if (log.isDebug()) {
            logger.debug("{}({})", method, args);
        } else {
            logger.info("{}({})", method, args);
        }
    }
}
