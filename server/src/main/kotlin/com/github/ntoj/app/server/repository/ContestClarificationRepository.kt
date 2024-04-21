package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.entities.ContestClarification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ContestClarificationRepository :
    JpaRepository<ContestClarification, Long>,
    JpaSpecificationExecutor<ContestClarification> {
    fun findAllByContestContestId(contestId: Long): List<ContestClarification>
}
