package com.github.ntoj.app.server.repository

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Lock
import com.github.ntoj.app.server.model.Submission
import com.github.ntoj.app.shared.model.JudgeStage
import java.util.Optional

interface SubmissionRepository : JpaRepository<Submission, Long>, JpaSpecificationExecutor<Submission> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findFirstByJudgeStageOrderBySubmissionId(stage: JudgeStage): Optional<Submission>
}
