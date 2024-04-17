package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.Problem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional

interface ProblemRepository : JpaRepository<Problem, Long>, JpaSpecificationExecutor<Problem> {
    fun findByAlias(alias: String): Optional<Problem>
}
