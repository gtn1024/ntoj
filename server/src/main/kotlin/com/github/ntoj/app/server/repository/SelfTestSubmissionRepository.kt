package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.entities.SelfTestSubmission
import com.github.ntoj.app.shared.model.JudgeStage
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Lock
import java.util.Optional

interface SelfTestSubmissionRepository : JpaRepository<SelfTestSubmission, Long>, JpaSpecificationExecutor<SelfTestSubmission> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findFirstByJudgeStageOrderBySelfTestSubmissionId(judgeStage: JudgeStage): Optional<SelfTestSubmission>
}
