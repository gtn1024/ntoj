package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.entities.JudgerSystemStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional

interface JudgerSystemStatusRepository :
    JpaRepository<JudgerSystemStatus, Long>,
    JpaSpecificationExecutor<JudgerSystemStatus> {
    fun findByJudgerId(judgerId: String): Optional<JudgerSystemStatus>
}
