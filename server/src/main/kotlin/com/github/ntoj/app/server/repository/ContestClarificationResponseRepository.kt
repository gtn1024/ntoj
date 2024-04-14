package com.github.ntoj.app.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import com.github.ntoj.app.server.model.ContestClarificationResponse

interface ContestClarificationResponseRepository :
    JpaRepository<ContestClarificationResponse, Long>,
    JpaSpecificationExecutor<ContestClarificationResponse>
