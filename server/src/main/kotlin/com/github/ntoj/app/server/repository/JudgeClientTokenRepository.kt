package com.github.ntoj.app.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import com.github.ntoj.app.server.model.JudgeClientToken
import java.util.Optional

interface JudgeClientTokenRepository :
    JpaRepository<JudgeClientToken, Long>,
    JpaSpecificationExecutor<JudgeClientToken> {
    fun findByToken(token: String): Optional<JudgeClientToken>

    fun existsByToken(token: String): Boolean
}
