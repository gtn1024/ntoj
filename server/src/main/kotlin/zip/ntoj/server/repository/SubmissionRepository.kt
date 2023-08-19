package zip.ntoj.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import zip.ntoj.server.model.Submission
import zip.ntoj.shared.model.JudgeStage
import java.util.Optional

interface SubmissionRepository : JpaRepository<Submission, Long>, JpaSpecificationExecutor<Submission> {
    fun findFirstByJudgeStageOrderBySubmissionId(stage: JudgeStage): Optional<Submission>
}
