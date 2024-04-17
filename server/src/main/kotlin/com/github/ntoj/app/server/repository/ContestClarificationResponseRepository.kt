package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.ContestClarificationResponse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ContestClarificationResponseRepository :
    JpaRepository<ContestClarificationResponse, Long>,
    JpaSpecificationExecutor<ContestClarificationResponse>
