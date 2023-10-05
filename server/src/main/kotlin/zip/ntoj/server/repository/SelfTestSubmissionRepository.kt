package zip.ntoj.server.repository

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Lock
import zip.ntoj.server.model.SelfTestSubmission
import zip.ntoj.shared.model.JudgeStage
import java.util.Optional

interface SelfTestSubmissionRepository : JpaRepository<SelfTestSubmission, Long>, JpaSpecificationExecutor<SelfTestSubmission> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findFirstByJudgeStageOrderBySelfTestSubmissionId(judgeStage: JudgeStage): Optional<SelfTestSubmission>
}
