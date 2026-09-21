package com.euphoriav.auth.aop.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Log(val isDebug: Boolean = false, val logArgs: Boolean = true)