package zip.ntoj.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import zip.ntoj.server.model.Submission
import java.util.Optional

interface SubmissionRepository : JpaRepository<Submission, Long>, JpaSpecificationExecutor<Submission> {
    fun findFirstByStatus(status: Submission.SubmissionStatus): Optional<Submission>
}
