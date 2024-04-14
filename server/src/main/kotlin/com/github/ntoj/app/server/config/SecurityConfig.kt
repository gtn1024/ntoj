package com.github.ntoj.app.server.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class SecurityConfig(
    @Value("\${sa-token.token-expire-timeout:604800}")
    val tokenExpireTime: Long = 604800L,
)
