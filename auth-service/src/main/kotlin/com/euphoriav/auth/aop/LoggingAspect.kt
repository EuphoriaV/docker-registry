package com.euphoriav.auth.aop

import com.euphoriav.auth.aop.annotation.Log
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class LoggingAspect {

    @Before("@annotation(log)")
    fun logMethodArgs(jp: JoinPoint, log: Log) {
        val logger = LoggerFactory.getLogger(jp.target.javaClass)
        val method = jp.signature.name
        val args = if (log.logArgs) jp.args.joinToString(", ") { it.toString() } else "..."
        if (log.isDebug) {
            logger.debug("{}({})", method, args)
        } else {
            logger.info("{}({})", method, args)
        }
    }
}