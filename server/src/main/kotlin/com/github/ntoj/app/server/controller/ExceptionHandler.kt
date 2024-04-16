package com.github.ntoj.app.server.controller

import cn.dev33.satoken.exception.NotLoginException
import cn.dev33.satoken.exception.SaTokenException
import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.ext.fail
import com.github.ntoj.app.shared.model.R
import com.github.ntoj.app.shared.util.randomString
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler(
    val logger: Logger = LoggerFactory.getLogger(ExceptionHandler::class.java),
) {
    @ExceptionHandler(AppException::class)
    fun handleTojException(e: AppException): ResponseEntity<R<Void>> {
        logger.error(e.message, e)
        return R.fail(e.code, e.message)
    }

    @ExceptionHandler(SaTokenException::class)
    fun handleSaTokenException(e: SaTokenException): ResponseEntity<R<Void>> {
        return when (e) {
            is NotLoginException -> {
                when (e.type) {
                    NotLoginException.INVALID_TOKEN,
                    NotLoginException.TOKEN_TIMEOUT,
                    NotLoginException.BE_REPLACED,
                    NotLoginException.KICK_OUT,
                    ->
                        R.fail(
                            401,
                            "token 已过期",
                        )

                    else -> R.fail(401, "未登录")
                }
            }

            else -> {
                val uuid = randomString()
                logger.error("$uuid - ${e.message}", e)
                R.fail(500, e.message ?: "未知错误", uuid)
            }
        }
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<R<Void>> {
        val uuid = randomString()
        logger.error("$uuid - ${e.message}", e)
        return R.fail(500, e.message ?: "未知错误", uuid)
    }
}
