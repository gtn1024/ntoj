package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.entities.JudgeClientToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional

interface JudgeClientTokenRepository :
    JpaRepository<JudgeClientToken, Long>,
    JpaSpecificationExecutor<JudgeClientToken> {
    fun findByToken(token: String): Optional<JudgeClientToken>

    fun existsByToken(token: String): Boolean
}
